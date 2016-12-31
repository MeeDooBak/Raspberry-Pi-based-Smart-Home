package Device;

import Rooms.*;

public interface DeviceInterface {

    public void ChangeState(boolean DeviceState);

    public void ChangeState(boolean DeviceState, String UP_DOWN);

    public void ChangeState(boolean DeviceState, int StepperMotorMoves);

    public void ChangeState(boolean DeviceState, int AlarmDuration, int AlarmInterval);

    public boolean getDeviceState();

    public boolean Capture(int TakeImage);

    public boolean Record(int Minute);

    public int getDeviceID();

    public RoomList getRoom();

    public String getDeviceName();
}
