package org.barrelmc.barrel.network.translator.bedrock;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import java.util.Collections;
import java.util.function.Function;

import com.github.steveice10.mc.protocol.data.game.chunk.BitStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.ChunkSection;
import com.github.steveice10.mc.protocol.data.game.chunk.DataPalette;
import com.github.steveice10.mc.protocol.data.game.chunk.palette.SingletonPalette;
import com.github.steveice10.mc.protocol.data.game.level.LightUpdateData;
import com.github.steveice10.mc.protocol.data.game.level.block.BlockEntityInfo;
import com.github.steveice10.mc.protocol.data.game.level.block.BlockEntityType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.io.stream.StreamNetOutput;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacket;

import org.barrelmc.barrel.network.converter.BlockConverter;
import org.barrelmc.barrel.network.translator.interfaces.BedrockPacketTranslator;
import org.barrelmc.barrel.player.Player;
import org.barrelmc.barrel.utils.nukkit.BitArray;
import org.barrelmc.barrel.utils.nukkit.BitArrayVersion;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LevelChunkPacket implements BedrockPacketTranslator {

    @Override
    public void translate(BedrockPacket pk, Player player) {

        ChunkSection chunkSection1 = new ChunkSection();
        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
        StreamNetOutput data1 = new StreamNetOutput(bos1);

        fillPalette(chunkSection1.getChunkData(), 0);
        fillPalette(chunkSection1.getBiomeData(), 0);
        try {
            ChunkSection.write(data1, chunkSection1, 4);
        } catch (Exception e) {
            //TODO: handle exception
        }
        byte[] emptyChunkSectionBytes = bos1.toByteArray();

        com.nukkitx.protocol.bedrock.packet.LevelChunkPacket packet = (com.nukkitx.protocol.bedrock.packet.LevelChunkPacket) pk;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamNetOutput data = new StreamNetOutput(bos);

        // use NetInput to make reading easier
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(packet.getData());

        try {
            ChunkSection[] chunkSections = new ChunkSection[16];
            int Length = 32;
            if(player.serverversion != "1.17.40"){
                for(int y = 0; y < 4; y++){
                    byte subchunkversion = byteBuf.readByte();
                    byteBuf.readByte(); //layer
                    if(subchunkversion >= 9){
                        byteBuf.readByte();
                    }
                }
                Length = packet.getSubChunksLength() - 4;
            }else{
                Length = packet.getSubChunksLength();
            }
            // read the blocks data
            for(int x = 0; x < Length; x++) {
                int version = (int)byteBuf.readByte();
                ChunkSection chunkSection = new ChunkSection();
                if (version == 0) {
                    readZeroChunk(byteBuf, chunkSection);
                } else if(version != 9) {
                    readChunk(byteBuf, chunkSection, x, byteBuf.readByte());
                }else{
                    byte storageSize = byteBuf.readByte();
                    byteBuf.readByte();
                    readChunk(byteBuf, chunkSection, x, storageSize);
                }
                chunkSections[x] = chunkSection;
            }

            // read the biome data
            /*boolean has0Ver = false;
            for(int x = 0; x < Length; x++) { // bedrock has hardcoded biome count to 24, but we just want to read the biome data with blocks in it
                int header = byteBuf.readByte();
                int paletteVersion = if(has0Ver) 0 else header or 1 shr 1
                val biomeData = chunkSections[it].biomeData
                if(paletteVersion != 0) {
                    val storage = PalettedStorage(byteBuf, header)

                    var index = 0
                    for (x in 0..3) {
                        for (y in 0..3) {
                            for (z in 0..3) {
                                val bedrockId = storage.get(x * 4, y * 4, z * 4)
                                val javaId = BiomeMappingHolder.javaToRuntime[BiomeMappingHolder.bedrockToJava[bedrockId] ?: "minecraft:the_void"] ?: 0

                                biomeData.storage[index] = biomeData.palette.stateToId(javaId).coerceAtLeast(0)

                                index++
                            }
                        }
                    }
                } else {
                    has0Ver = true
                    fillPalette(biomeData) // todo: translate palette with version 0
                }
            }*/

            for(ChunkSection it : chunkSections) {
                try {
                    ChunkSection.write(data, it, 4);
                } catch (Exception e) {
                    //TODO: handle exception
                }
                
            }
        } finally {
            byteBuf.release(); // make sure to release the buffer or the memory will leak
        }
        int Length = packet.getSubChunksLength();
        if(player.serverversion != "1.17.40"){
            Length -= 4;
        }
        int sectionLast = 256 - Length;
        if(sectionLast > 0) {
            for(int x = 0; x < 256; x++) {
                try {
                    data.write(emptyChunkSectionBytes);
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }

        // just send an empty light data, client will calculate light itself
        LightUpdateData light =  new LightUpdateData(new BitSet(), new BitSet(), new BitSet(), new BitSet(), Collections.emptyList(), Collections.emptyList(), true);
        // todo: calculate height map to make client load chunk faster
        try {
            bos.close();
            data.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
        byte [] cdata = bos.toByteArray();
        System.out.println(cdata.length);
        player.javaSession.send(new ClientboundLevelChunkWithLightPacket(packet.getChunkX(), packet.getChunkZ(), cdata, new CompoundTag("MOTION_BLOCKING"), new BlockEntityInfo[]{new BlockEntityInfo(1, 0, 1, BlockEntityType.CHEST, null)},  light));
    }

    /**
     * chunk with version 1 or 8 is used in modern bedrock servers
     */
    public void readChunk(ByteBuf byteBuf, ChunkSection chunkSections, int sectionIndex, byte storageSize) {
        for (int storageReadIndex = 0; storageReadIndex < storageSize; storageReadIndex++) {
            byte paletteHeader = byteBuf.readByte();
            int paletteVersion = (paletteHeader | 1) >> 1;

            BitArrayVersion bitArrayVersion = BitArrayVersion.get(paletteVersion, true);

            int maxBlocksInSection = 4096;
            BitArray bitArray = bitArrayVersion.createPalette(maxBlocksInSection);
            int wordsSize = bitArrayVersion.getWordsForSize(maxBlocksInSection);

            for (int wordIterationIndex = 0; wordIterationIndex < wordsSize; wordIterationIndex++) {
                int word = byteBuf.readIntLE();
                bitArray.getWords()[wordIterationIndex] = word;
            }

            int paletteSize = VarInts.readInt(byteBuf);
            int[] sectionPalette = new int[paletteSize];
            for (int i = 0; i < paletteSize; i++) {
                sectionPalette[i] = VarInts.readInt(byteBuf);
            }

            if (storageReadIndex == 0) {
                int index = 0;
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            int paletteIndex = bitArray.get(index);
                            int mcbeBlockId = sectionPalette[paletteIndex];

                            if (mcbeBlockId != 0) {
                                chunkSections.setBlock(x, y, z, BlockConverter.toJavaStateId(mcbeBlockId));
                            }
                            index++;
                        }
                    }
                }
            }
        }
    }

    /**
     * chunk with version 0 is used in PocketMine-MP
     */
    private void readZeroChunk(ByteBuf byteBuf, ChunkSection chunkSection) {
        byte[] blockIds = new byte[4096];
        byteBuf.readBytes(blockIds);

        byte[] metaIds = new byte[2048];
        byteBuf.readBytes(metaIds);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int index = (x << 8) + (z << 4) + y;

                    int id = blockIds[index];
                    int meta = metaIds[index >> 1] >> (index & 1) * 4 & 15;

                    chunkSection.setBlock(x, y, z, BlockConverter.toJavaStateId(id));
                }
            }
        }
    }

    /**
     * fill the palette with specified value
     */
    private void fillPalette(DataPalette dataPalette, int state) {
        BitStorage bitStorage = dataPalette.getStorage();
        dataPalette.setPalette(new SingletonPalette(state));
        for(int x = 0; x < bitStorage.getSize(); x++) {
            bitStorage.set(x, 0); // singleton palette, so all bits are 0
        }
    }
}
