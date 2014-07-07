package gmail.wumbleminky.powerlines.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPole extends TileEntity {
	
	public static String ID = "tileEntityPole"; 

	// NBT Fields
	public String side;
	private int[] base_coords;
	private int[] pole1_coords;
	private int[] pole2_coords;
	
	//flags
	public boolean needs_update;
	private boolean _update_neighbors;
	public boolean pole_change;
	
	public TileEntityPole base_pole;
	public List<TileEntityPole> connected_poles;
	
	public int redstone_output; //the outputed redstone signal (to other poles)
	public TileEntityPole redstone_input; //the pole that is sending me redstone
	
	public TileEntityPole(){
		side = ForgeDirection.UNKNOWN.toString();
		needs_update = true;
		pole_change = false;
		connected_poles = new ArrayList<TileEntityPole>();
	}
	
	@Override
	public void validate(){
		super.validate();
	}
	
	@Override
	public void updateEntity(){
		
		//one time updates
		if (base_coords != null){
			base_pole = getPoleAt(base_coords[0], base_coords[1], base_coords[2]);
			base_coords = null;
		}
		
		if (pole1_coords != null){
			TileEntityPole te1 = getPoleAt(pole1_coords[0], pole1_coords[1], pole1_coords[2]);
			setConnectedPole(te1);
			pole1_coords = null;
		}
		
		if (pole2_coords != null){
			TileEntityPole te2 = getPoleAt(pole2_coords[0], pole2_coords[1], pole2_coords[2]);
			setConnectedPole(te2);
			pole2_coords = null;
		}
		
		if (base_pole == null){
			base_pole = this;
		}
		
		//regular update
		
		if (pole_change){
			updateNeighbors();
			updateRedstoneOutput(getRedstoneOutput());
			pole_change = false;
		}else if (isReceivingRedstone() && needs_update){
			ForgeDirection d = getSide();
			if (d != ForgeDirection.UNKNOWN){
				if (worldObj.isBlockProvidingPowerTo(this.xCoord + d.offsetX, this.yCoord + d.offsetY, this.zCoord + d.offsetZ, d.ordinal()) == 0){
					//Was emitting redstone, but now there is no signal
					setSide(ForgeDirection.UNKNOWN);
					updateNeighbors();
					updateRedstoneOutput(0);
				}
			}
			needs_update = false;
		}else if (redstone_input == null && needs_update){
			// check for redstone signals
			for (ForgeDirection d: ForgeDirection.VALID_DIRECTIONS){
				if (worldObj.isBlockProvidingPowerTo(this.xCoord + d.offsetX, this.yCoord + d.offsetY, this.zCoord + d.offsetZ, d.ordinal()) > 0){
					setSide(d);
					updateNeighbors();
					updateRedstoneOutput(15);
					break;
				}
			}
			needs_update = false;
		}
		
		
		//update my neighbors
		if (_update_neighbors){
			Block b = worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
			for(ForgeDirection d: ForgeDirection.VALID_DIRECTIONS){
				worldObj.notifyBlockOfNeighborChange(xCoord + d.offsetX, yCoord + d.offsetY, zCoord + d.offsetZ, b);
				worldObj.notifyBlocksOfNeighborChange(xCoord + d.offsetX, yCoord + d.offsetY, zCoord + d.offsetZ, b);
			}
			_update_neighbors = false;
		}
	}
	
	public ForgeDirection getSide(){
		return ForgeDirection.valueOf(side);
	}
	
	public void setSide(String side){
		this.side = side;
	}
	
	public void setSide(ForgeDirection side){
		setSide(side.toString());
	}
	
	public boolean isReceivingRedstone(){
		//Is the pole receiving a direct redstone signal.
		if (!side.equalsIgnoreCase(ForgeDirection.UNKNOWN.toString())){
			return true;
		}
		return false;
	}
	
	
	public boolean isConnectedReceiving(){
		//Is the pole receiving redstone from a connected pole
		return redstone_input != null;
//		if (getPole1() != null && getPole1().redstone_output > 0){
//			return true;
//		}else if (getPole2() != null && getPole2().redstone_output > 0){
//			return true;
//		}
//		return false;
	}
	
	public int getRedstoneOutput(){
		//the redstone signal I am outputing
		if (isReceivingRedstone()){
			return 15;
		}else if(redstone_input != null){
			return redstone_input.redstone_output;
		}
		return 0;
	}
	
	public int getRedstoneOutput(int side){
		//get the redstone output for the given side. Only really applicable if I am receiving
		// a direct redstone signal
		if (isReceivingRedstone()){
			ForgeDirection f_side = ForgeDirection.getOrientation(side).getOpposite();
			if (f_side != getSide()){
				return 15;
			}else{
				return 0;
			}
		}
		return getRedstoneOutput();
	}
	
	public void updateRedstoneOutput(int output){
		//update my outputing redstone, then update my connected poles
		redstone_output = output;
		for (TileEntityPole p: connected_poles){
			if (p != redstone_input && p.redstone_input == null && redstone_output > 0){
					p.redstone_input = this;
					p.pole_change = true;
			}else if(p.redstone_input == this && redstone_output == 0){
					p.redstone_input = null;
					p.pole_change = true;
			}
		}
	}
	
	public boolean isSegmentInPole(TileEntityPole segment){
		return getEntirePole().contains(segment);
	}
	
	private void updateNeighbors(){
		_update_neighbors = true;
	}
	
	public List<TileEntityPole> getEntirePole(){
		//get a list of the entire pole (myself and all the other TileEntityPole's directly above and/or below).
		List<TileEntityPole> pole = new ArrayList<TileEntityPole>();
		TileEntityPole current = getBase();
		while (current != null){
			pole.add(current);
			current = getPoleAt(current.xCoord, current.yCoord + 1, current.zCoord);
		}
		return pole;
	}
	
	public void updatePole(){
		//find the base pole, then update all poles.
		TileEntityPole te_base = findBasePole();
		te_base.updatePoleBase(te_base);
	}
	
	public void updatePoleBase(TileEntityPole new_base){
		//update the pole with a new base pole. then update the base of a TileEntityPole above (if found)
		if (new_base != base_pole){
			setBase(new_base);
		}
		TileEntityPole te = getPoleAt(xCoord, yCoord + 1, zCoord);
		if (te != null) {
			te.updatePoleBase(new_base);
		}
	}
	
	public TileEntityPole findBasePole(){
		//search for the lowest TileEntityPole in the pole.
		TileEntityPole te = getPoleAt(xCoord, yCoord - 1, zCoord);
		if (te != null){
			return te.findBasePole();
		}
		return this;
	}
	
	public TileEntityPole getBase(){
		return base_pole;
	}
	
	public boolean hasPole1(){
		return getPole1() != null;
	}
	
	public boolean hasPole2(){
		return getPole2() != null;
	}
	
	public TileEntityPole getPole1(){
		if (!connected_poles.isEmpty()){
			return connected_poles.get(0);
		}
		return null;
	}
	
	public TileEntityPole getPole2(){
		if (connected_poles.size() > 1){
			return connected_poles.get(1);
		}
		return null;
	}
	
	private TileEntityPole getPoleAt(int x, int y, int z){
		return (TileEntityPole) worldObj.getTileEntity(x, y, z);
	}
	
	public boolean isConnected(){
		return !connected_poles.isEmpty();
	}
	
	public void connectPoles(TileEntityPole other_pole_te){
		//connects the two poles together
		setConnectedPole(other_pole_te);
		other_pole_te.setConnectedPole(this);
	}
	
	public void setConnectedPole(TileEntityPole connected_te){
		// adds the give TileEntityPole to the connected_poles. removes the first pole if there are already 2 connections.
		if (connected_poles.size() >= 2){
			connected_poles.remove(0);
		}
		connected_poles.add(connected_te);
	}
	
	public void removeConnectedPole(TileEntityPole te){
		connected_poles.remove(te);
	}
	
	public boolean isBase(){
		return this == base_pole; 
	}
	
	public boolean hasSameBasePole(TileEntityPole base){
		return base == base_pole;
	}
	
	public void setBase(TileEntityPole te){
		base_pole = te;
	}
	
	public void blockBroken(){
		//remove myself from my connected poles.
		if (hasPole1()){
			getPole1().removeConnectedPole(this);
		}
		if (hasPole2()){
			getPole2().removeConnectedPole(this);
		}
		updatePole();
	}
	
	//**************** NBT Methods
	
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("side", side);
        int[] base_coords = new int[3];
        base_coords[0] = base_pole.xCoord;
        base_coords[1] = base_pole.yCoord;
        base_coords[2] = base_pole.zCoord;
        nbt.setIntArray("base_coords", base_coords);
        
        //save the pole coordinates if there is a pole1 and pole2
        if (hasPole1()){
        	int[] pole1_coords = new int[3];
        	pole1_coords[0] = getPole1().xCoord;
        	pole1_coords[1] = getPole1().yCoord;
        	pole1_coords[2] = getPole1().zCoord;
        	nbt.setIntArray("pole1_coords", pole1_coords);
        }
        if (hasPole2()){
        	int[] pole2_coords = new int[3];
        	pole2_coords[0] = getPole2().xCoord;
        	pole2_coords[1] = getPole2().yCoord;
        	pole2_coords[2] = getPole2().zCoord;
        	nbt.setIntArray("pole2_coords", pole2_coords);
        }
    }
	
	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.side = nbt.getString("side");
        base_coords = nbt.getIntArray("base_coords"); 
        if (nbt.hasKey("pole1_coords")){
        	pole1_coords = nbt.getIntArray("pole1_coords");
        }
        if (nbt.hasKey("pole2_coords")){
        	pole2_coords = nbt.getIntArray("pole2_coords");
        }
    }
}
