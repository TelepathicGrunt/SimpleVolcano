package com.telepathicgrunt.simplevolcano.world.features;

import com.telepathicgrunt.simplevolcano.SimpleVolcano;
import com.telepathicgrunt.simplevolcano.world.features.structures.Volcano;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;


public class FeatureInit
{
	//Static instance of our structure so we can reference it and add it to biomes easily.
	public static Feature<NoFeatureConfig> VOLCANO = new Volcano(NoFeatureConfig::deserialize);


	/*
	 * Registers the features and structures. Normal Features will be registered here too.
	 */
	public static void registerFeatures(Register<Feature<?>> event)
	{
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		//Registers the structure itself and sets what its path is. In this case,
		//the structure will have the resourcelocation of structure_tutorial:run_down_house .
		//
		//Also, the path string you give will be what the user will see when they do the
		//locate command.
		SimpleVolcano.register(registry, VOLCANO, "volcano");
	}
}
