package com.telepathicgrunt.simplevolcano.world.features.structures;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;
import com.telepathicgrunt.simplevolcano.OpenSimplexNoise;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;


public class Volcano extends Feature<NoFeatureConfig>
{
	protected long seed;
	protected OpenSimplexNoise noiseGen;
	private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
	private static final BlockState POWDER = Blocks.LIGHT_GRAY_CONCRETE_POWDER.getDefaultState();
	private static final BlockState SNOW = Blocks.SNOW_BLOCK.getDefaultState();
	private static final BlockState DARK_POWDER = Blocks.GRAY_CONCRETE_POWDER.getDefaultState();
	private static final BlockState BLACK_POWDER = Blocks.BLACK_CONCRETE_POWDER.getDefaultState();
	private static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();

	public void setSeed(long seed)
	{
		if (this.seed != seed || this.noiseGen == null)
		{
			this.noiseGen = new OpenSimplexNoise(seed);
			this.seed = seed;
		}
	}

	
	public Volcano(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory)
	{
		super(configFactory);
	}


	@Override
	public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> changedBlock, Random rand, BlockPos position, NoFeatureConfig config)
	{
		setSeed(world.getSeed());

		double noise;
		double noise2;
		int maximumHeight;
		int terrainHeight;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				boolean topBlock = true;
				boolean insideCone = false;
				mutable.setPos(position.getX() + x, 0, position.getZ() + z);
				noise2 = (noiseGen.eval(mutable.getX()*0.04D, mutable.getY()*0.04D, mutable.getZ()*0.04D)+1D)*5D;
				noise = Math.pow(Math.abs(noiseGen.eval(mutable.getX()*0.003D, mutable.getZ()*0.003D)) + noise2*0.005D, 7); //0.70990733195111407153665966708847
				maximumHeight = (int) (noise*700D);
				terrainHeight= world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, mutable.getX(), mutable.getZ());
				if(maximumHeight > 140+noise2)
				{
					maximumHeight = (int) Math.max((maximumHeight-(maximumHeight-140-noise2)*2), terrainHeight);
					insideCone = true;
				}
				mutable.move(Direction.UP, maximumHeight);
				for(int y = maximumHeight; y >= terrainHeight; y--)
				{
					noise2 = (noiseGen.eval(mutable.getX()*0.045D, y*0.04D, mutable.getZ()*0.045D)+1D)*10;
					if(topBlock)
					{
						if(insideCone)
						{
							if(y < 101 + noise2)
							{
								world.setBlockState(mutable, MAGMA_BLOCK, 2);
							}
							else
							{
								world.setBlockState(mutable, DARK_POWDER, 2);
							}
							
							for(int lavaY = 0; lavaY < 100 - y; lavaY++)
							{
								world.setBlockState(mutable.up(lavaY), LAVA, 2);
							}
						}
						else 
						{
							Biome biome = world.getBiome(mutable);
							if(y+noise2 > 133)
							{
								world.setBlockState(mutable, SNOW, 2);
							}
							else if(y+noise2 > 105)
							{
								world.setBlockState(mutable, POWDER, 2);
							}
							else if(y+noise2 > 95)
							{
								if(!biome.getCategory().equals(Category.DESERT) && biome.getCategory().equals(Category.MESA))
								{
									world.setBlockState(mutable, PODZOL, 2);
								}
								else
								{
									world.setBlockState(mutable, biome.getSurfaceBuilderConfig().getUnder(), 2);
								}
							}
							else if(y+noise2 > 75)
							{
								world.setBlockState(mutable, biome.getSurfaceBuilderConfig().getTop(), 2);
							}
							else if(y+noise2/3 > 63)
							{
								world.setBlockState(mutable, SAND, 2);
							}
							else
							{
								world.setBlockState(mutable, GRAVEL, 2);
							}
						}
						
						topBlock = false;
					}
					else
					{
						if(insideCone && maximumHeight - y < 140+rand.nextInt(rand.nextInt(7)+1) - maximumHeight)
						{
							world.setBlockState(mutable, BLACK_POWDER, 2);
						}
						else
						{
							world.setBlockState(mutable, STONE, 2);
						}
					}
					
					mutable.move(Direction.DOWN);
				}
			}
		}
		
		return true;
	}

}
