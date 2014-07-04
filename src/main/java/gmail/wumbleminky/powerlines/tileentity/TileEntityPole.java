package gmail.wumbleminky.powerlines.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPole extends TileEntity {

	// NBT Fields
	public String side;
	public int[] base_coords;
	public int[] pole1_coords;
	public int[] pole2_coords;
	
	//other fields
	public boolean needsUpdate;
	public boolean stillUpdating;
	private boolean _updateNeighbors;
	public boolean pole1_change;
	public boolean pole2_change;
	
	public static String ID = "tileEntityPole"; 
	
	public TileEntityPole(){
		side = ForgeDirection.UNKNOWN.toString();
		needsUpdate = true;
		stillUpdating = false;
		pole1_change = false;
		pole2_change = false;
	}
	
	@Override
	public void validate(){
		super.validate();
	}
	
	@Override
	public void updateEntity(){
		
		if (base_coords == null){
			base_coords = new int[3];
			base_coords[0] = xCoord;
			base_coords[1] = yCoord;
			base_coords[2] = zCoord;
		}
		
		if (needsUpdate){
			if (pole1_change || pole2_change){
				updateNeighbors();
				updateRedstoneOutput();
				pole1_change = false;
				pole2_change = false;
			}else if (isReceivingRedstone()){
				ForgeDirection d = getSide();
				if (d != ForgeDirection.UNKNOWN){
					if (worldObj.isBlockProvidingPowerTo(this.xCoord + d.offsetX, this.yCoord + d.offsetY, this.zCoord + d.offsetZ, d.ordinal()) == 0){
						//Was emitting redstone, but now there is no signal
						setSide(ForgeDirection.UNKNOWN);
						updateNeighbors();
						updateRedstoneOutput();
					}
				}
			}else if (!(pole1_coords != null && pole1_coords[4] > 0) && !(pole2_coords != null && pole2_coords[4] > 0)) {
				// check for redstone signals
				for (ForgeDirection d: ForgeDirection.VALID_DIRECTIONS){
					if (worldObj.isBlockProvidingPowerTo(this.xCoord + d.offsetX, this.yCoord + d.offsetY, this.zCoord + d.offsetZ, d.ordinal()) > 0){
						setSide(d);
						updateNeighbors();
						updateRedstoneOutput();
						break;
					}
				}
			}
			
			this.needsUpdate = false;
		}
		
		if (_updateNeighbors){
			Block b = worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
			for(ForgeDirection d: ForgeDirection.VALID_DIRECTIONS){
				worldObj.notifyBlockOfNeighborChange(xCoord + d.offsetX, yCoord + d.offsetY, zCoord + d.offsetZ, b);
				worldObj.notifyBlocksOfNeighborChange(xCoord + d.offsetX, yCoord + d.offsetY, zCoord + d.offsetZ, b);
			}
			_updateNeighbors = false;
		}
		if (stillUpdating){
			stillUpdating = false;
		}
	}
	
	public ForgeDirection getSide(){
		return ForgeDirection.valueOf(side);
	}
	
	public boolean isReceivingRedstone(){
		if (!side.equalsIgnoreCase(ForgeDirection.UNKNOWN.toString())){
			return true;
		}
		return false;
	}
	
	public boolean isConnectedReceiving(){
		if (pole1_coords != null && pole1_coords[4] > 0){
			return true;
		}else if (pole2_coords != null && pole2_coords[4] > 0){
			return true;
		}
		return false;
	}
	
	public int getRedstoneOutput(){
		int ret_val = 0;
		if (isReceivingRedstone()){
			ret_val = 15;
		}else{ 
			if (pole1_coords != null){
				ret_val = Math.max(ret_val, pole1_coords[4]);
			}
			if (pole2_coords != null){
				ret_val = Math.max(ret_val, pole2_coords[4]);
			}
		}
		return ret_val;
	}
	
	public int getRedstoneOutput(int side){
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
	
	public void updateRedstoneOutput(){
		if (pole1_coords != null && pole1_coords[4] == 0 && !pole1_change){
			getPole1().updateFromPole(this, getRedstoneOutput());
		}
		if (pole2_coords != null && pole2_coords[4] == 0 && !pole2_change){
			getPole2().updateFromPole(this, getRedstoneOutput());
		}
	}
	
	public void updateFromPole(TileEntityPole pole, int value){
		if (getPole1() == pole && pole1_coords[4] != value){
			pole1_coords[4] = value;
			pole1_change = true;
			updateNeighbors();
		}
		if (getPole2() == pole && pole2_coords[4] != value){
			pole2_coords[4] = value;
			pole2_change = true;
			updateNeighbors();
		}
	}
	
	public boolean isSegmentInPole(TileEntityPole segment){
		return getEntirePole().contains(segment);
	}
	
//	public void updateConnectedRedstoneOutput(TileEntityPole pole, int redstone){
//		if (getPole1() == pole){
//			pole1_coords[4] = redstone;
//			updateNeighbors();
//		}
//		if (getPole2() == pole){
//			pole2_coords[4] = redstone;
//			updateNeighbors();
//		}
//	}
	
	public void setSide(ForgeDirection side){
		setSide(side.toString());
	}
	
	public void setSide(String side){
		this.side = side;
	}
	
	private void updateNeighbors(){
		_updateNeighbors = true;
	}
	
	public List<TileEntityPole> getEntirePole(){
		List<TileEntityPole> pole = new ArrayList<TileEntityPole>();
		TileEntityPole current = getBase();
		while (current != null){
			pole.add(current);
			current = (TileEntityPole) worldObj.getTileEntity(current.xCoord, current.yCoord + 1, current.zCoord);
		}
		return pole;
	}
	
	public void updatePole(){
		TileEntityPole te_base = findBasePole();
		te_base.updatePoleBase(te_base);
	}
	
	public void updatePoleBase(TileEntityPole new_base){
		
		if (new_base.base_coords != this.base_coords){
			setBase(new_base);
		}
		TileEntityPole te = (TileEntityPole) worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
		if (te != null) {
			te.updatePoleBase(new_base);
		}
	}
	
	public TileEntityPole findBasePole(){
		TileEntityPole te = (TileEntityPole) worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
		if (te != null){
			return te.findBasePole();
		}
		return this;
	}
	
	public TileEntityPole getBase(){
		return getPoleAt(base_coords);
	}
	
	public TileEntityPole getPole1(){
		if (pole1_coords != null){
			return getPoleAt(pole1_coords);
		}
		return null;
	}
	
	public TileEntityPole getPole2(){
		if (pole2_coords != null){
			return getPoleAt(pole2_coords);
		}
		return null;
	}
	
	private TileEntityPole getPoleAt(int[] coords){
		return (TileEntityPole) worldObj.getTileEntity(coords[0], coords[1], coords[2]);
	}
	
	public List<TileEntityPole> getAllConnectedPoles(){
		List<TileEntityPole> all_connected_poles = new ArrayList<TileEntityPole>();
		TileEntityPole current = getBase();
		while (current != null){
			if (current.pole1_coords != null){
				all_connected_poles.add(current.getPole1());
			}
			if (current.pole2_coords != null){
				all_connected_poles.add(current.getPole2());
			}
			current = (TileEntityPole)worldObj.getTileEntity(current.xCoord, current.yCoord + 1, current.zCoord);
		}
		return all_connected_poles;
	}
	
	public boolean isConnected(){
		if (pole1_coords != null || pole2_coords != null){
			return true;
		}
		return false;
	}
	
	public void connectPoles(TileEntityPole other_pole_te){
		setConnectedPole(other_pole_te);
		other_pole_te.setConnectedPole(this);
	}
	
	public void setConnectedPole(TileEntityPole connected_te){
		
		if (pole1_coords != null && pole2_coords != null){
			if (pole2_coords[3] < pole1_coords[3]){
				pole2_coords = null;
				pole1_coords[3] = 0;
			}else{
				pole1_coords = null;
				pole2_coords[3] = 0;
			}
		}
		
		if (pole1_coords == null){
			pole1_coords = new int[5];
			pole1_coords[0] = connected_te.xCoord;
			pole1_coords[1] = connected_te.yCoord;
			pole1_coords[2] = connected_te.zCoord;
			if (pole2_coords == null){
				pole1_coords[3] = 0;
			}else{
				pole1_coords[3] = 1;
			}
			pole1_coords[4] = connected_te.getRedstoneOutput();
		}else if (pole2_coords == null){
			pole2_coords = new int[5];
			pole2_coords[0] = connected_te.xCoord;
			pole2_coords[1] = connected_te.yCoord;
			pole2_coords[2] = connected_te.zCoord;
			if (pole1_coords == null){
				pole2_coords[3] = 0;
			}else{
				pole2_coords[3] = 1;
			}
			pole2_coords[4] = connected_te.getRedstoneOutput();
		}
	}
	
	public void removeConnectedPole(TileEntityPole te){
		if (pole1_coords != null && getPole1().equals(te)){
			pole1_coords = null;
		}
		if (pole2_coords != null && getPole2().equals(te)){
			pole2_coords = null;
		}
	}
	
	public boolean isBase(){
		return this.xCoord == base_coords[0] && this.yCoord == base_coords[1] && this.zCoord == base_coords[2]; 
	}
	
	public boolean hasSameBasePole(TileEntityPole base){
		
		for (int i = 0; i < 3; i++){
			if (base.base_coords[i] != this.base_coords[i]){
				return false;
			}
		}
		return true;
	}
	
	public void setBase(TileEntityPole te){
		setBase(te.xCoord, te.yCoord, te.zCoord);
	}
	
	public void setBase(int x, int y, int z){
		System.out.println("update base: " + x + "," + y + "," + z);
		if (base_coords == null){
			base_coords = new int[6];
		}
		base_coords[0] = x;
		base_coords[1] = y;
		base_coords[2] = z;
	}
	
	public void blockBroken(){
		TileEntityPole pole1 = getPole1();
		TileEntityPole pole2 = getPole2();
		if (pole1 != null){
			pole1.removeConnectedPole(this);
//			pole1.getNetwork().needsUpdate = true;
		}
		if (pole2 != null){
			pole2.removeConnectedPole(this);
//			pole2.getNetwork().needsUpdate = true;
		}
		updatePole();
//		getNetwork().needsUpdate = true;
	}
	
	//**************** NBT Methods
	
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("side", side);
        nbt.setIntArray("base_coords", base_coords);
        if (pole1_coords != null){
        	nbt.setIntArray("pole1_coords", pole1_coords);
        }
        if (pole2_coords != null){
        	nbt.setIntArray("pole2_coords", pole2_coords);
        }
    }
	
	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.side = nbt.getString("side");
        this.base_coords = nbt.getIntArray("base_coords");
        if (nbt.hasKey("pole1_coords")){
        	this.pole1_coords = nbt.getIntArray("pole1_coords");
        }
        if (nbt.hasKey("pole2_coords")){
        	this.pole2_coords = nbt.getIntArray("pole2_coords");
        }
    }
}
