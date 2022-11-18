package com.brewinandchewin.common.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CheeseWheelBlock extends Block
{
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 1);
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
	public final Supplier<Block> ripeCheese;
	
	public CheeseWheelBlock(Supplier<Block> ripeCheese, Properties properties) {
		super(properties);
		this.registerDefaultState(super.defaultBlockState().setValue(AGE, 0));
		this.ripeCheese = ripeCheese;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE; 
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, facingState, level, pos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return !level.isEmptyBlock(pos.below());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if (worldIn.isClientSide) return;
		if (worldIn.getRandom().nextFloat() <= 0.1F) {
			if (state.getValue(AGE) == 0) {
				worldIn.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 3); // next stage
			}
			if (state.getValue(AGE) == 1) {
				worldIn.setBlock(pos, ripeCheese.get().defaultBlockState(), 3); // next stage
			}
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return (2 - blockState.getValue(AGE));
	}
}