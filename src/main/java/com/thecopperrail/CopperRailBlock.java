package com.thecopperrail;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CopperRailBlock extends PoweredRailBlock {
    public CopperRailBlock(Settings settings){
        super(settings);
        setDefaultState(getDefaultState().with(Properties.INVERTED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
	    assert state != null;
	    boolean isInverted = switch(state.get(getShapeProperty())) {
            case EAST_WEST -> ctx.getHorizontalPlayerFacing() == Direction.EAST;
            case NORTH_SOUTH -> ctx.getHorizontalPlayerFacing() == Direction.SOUTH;
            default -> throw new UnsupportedOperationException();
        };
        return state.with(Properties.INVERTED, isInverted);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, WATERLOGGED, Properties.INVERTED);
    }

    /** @return Normalized push direction assuming powered. */
    static public Vec3d getPushForce(BlockState railState) {
        Vec3d pushForce = switch(railState.get(SHAPE)){
            case ASCENDING_EAST, ASCENDING_WEST, EAST_WEST -> new Vec3d(1,0,0);
            case ASCENDING_SOUTH, ASCENDING_NORTH, NORTH_SOUTH -> new Vec3d(0,0,1);
            default -> throw new UnsupportedOperationException();
        };
        if(railState.get(Properties.INVERTED))
            pushForce = pushForce.negate();
        return pushForce;
    }
}