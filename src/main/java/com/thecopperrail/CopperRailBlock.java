package com.thecopperrail;

import net.minecraft.item.Item;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CopperRailBlock extends PoweredRailBlock {
    public static final Block BLOCK = new CopperRailBlock(
        AbstractBlock.Settings.create()
        .strength(0.7f, 0.7f)
        .sounds(BlockSoundGroup.METAL)
        .noCollision()
        .requiresTool()
    );

    public static final BlockItem BLOCK_ITEM = new BlockItem(BLOCK, new Item.Settings());

    public CopperRailBlock(Settings settings){
        super(settings);
        setDefaultState(getDefaultState().with(Properties.INVERTED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        boolean isInverted = switch(state.get(getShapeProperty())) {
            case EAST_WEST -> ctx.getHorizontalPlayerFacing() == Direction.EAST;
            case NORTH_SOUTH -> ctx.getHorizontalPlayerFacing() == Direction.SOUTH;
            default -> throw new UnsupportedOperationException();
        };
        return state.with(Properties.INVERTED, isInverted);
    }

    public Vec3d getPushVector(BlockState state) {
        return switch(state.get(getShapeProperty())){
			case ASCENDING_EAST, ASCENDING_WEST, EAST_WEST -> new Vec3d(.5,0,0);
            case ASCENDING_SOUTH, ASCENDING_NORTH, NORTH_SOUTH -> new Vec3d(0,0,.5);
			default -> throw new UnsupportedOperationException();
        };
    }

    public void affectMinecart(AbstractMinecartEntity minecart, BlockState state){
        Vec3d pushForce = getPushVector(state);
        if(state.get(Properties.INVERTED))
            pushForce = pushForce.negate();
        minecart.setVelocity(minecart.getVelocity().add(pushForce));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SHAPE, POWERED, WATERLOGGED, Properties.INVERTED});
    }
}