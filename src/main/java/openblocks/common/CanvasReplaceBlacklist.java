package openblocks.common;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openmods.config.properties.ConfigurationChange;

public class CanvasReplaceBlacklist {
	private Set<ResourceLocation> idBlacklist = createBlacklist();

	private static Set<ResourceLocation> createBlacklist() {
		return Arrays.asList(Config.canvasBlacklist).stream().map(ResourceLocation::new).collect(Collectors.toSet());
	}

	public static final CanvasReplaceBlacklist instance = new CanvasReplaceBlacklist();

	public boolean isAllowedToReplace(IBlockState state) {
		final Block block = state.getBlock();
		if (block.hasTileEntity(state)) return false;

		if (filterVanillaBlocks(block)) return false;

		return !idBlacklist.contains(block.getRegistryName());
	}

	private static boolean filterVanillaBlocks(Block block) {
		// two-part blocks do not work nice with canvas
		return block instanceof BlockDoor ||
				block instanceof BlockBed;
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("canvas", "replaceBlacklist")) idBlacklist = createBlacklist();
	}

	public boolean isAllowedToReplace(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) { return false; }
		return isAllowedToReplace(world.getBlockState(pos));
	}
}
