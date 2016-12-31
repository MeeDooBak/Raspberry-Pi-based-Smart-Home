package _Testing;

import com.pi4j.component.motor.impl.*;
import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;
import java.util.Scanner;
import java.util.logging.*;

public class NewMain {

    public static void main(String[] args) {
        try {
            GpioController GPIO = GpioFactory.getInstance();
            MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
            MCP23017GpioProvider Provider2 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);

            GpioPinDigitalOutput[] PINS1 = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A0, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A1, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A2, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A3, PinState.LOW)};

            GpioPinDigitalOutput[] PINS2 = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A4, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A5, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A6, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_A7, PinState.LOW)};

            GpioPinDigitalOutput[] PINS3 = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B0, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B1, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B2, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B3, PinState.LOW)};

            GpioPinDigitalOutput[] PINS4 = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B4, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B5, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B6, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.GPIO_B7, PinState.LOW)};

            GpioPinDigitalOutput[] PINS5 = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(Provider2, MCP23017Pin.GPIO_B0, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider2, MCP23017Pin.GPIO_B1, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider2, MCP23017Pin.GPIO_B2, PinState.LOW),
                GPIO.provisionDigitalOutputPin(Provider2, MCP23017Pin.GPIO_B3, PinState.LOW)};

            GPIO.setShutdownOptions(true, PinState.LOW, PINS1);
            GPIO.setShutdownOptions(true, PinState.LOW, PINS2);
            GPIO.setShutdownOptions(true, PinState.LOW, PINS3);
            GPIO.setShutdownOptions(true, PinState.LOW, PINS4);
            GPIO.setShutdownOptions(true, PinState.LOW, PINS5);

            GpioStepperMotorComponent Motor1 = new GpioStepperMotorComponent(PINS1);
            GpioStepperMotorComponent Motor2 = new GpioStepperMotorComponent(PINS2);
            GpioStepperMotorComponent Motor3 = new GpioStepperMotorComponent(PINS3);
            GpioStepperMotorComponent Motor4 = new GpioStepperMotorComponent(PINS4);
            GpioStepperMotorComponent Motor5 = new GpioStepperMotorComponent(PINS5);

            Motor1.setStepInterval(2);
            Motor2.setStepInterval(2);
            Motor3.setStepInterval(2);
            Motor4.setStepInterval(2);
            Motor5.setStepInterval(2);

            Motor1.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
            Motor2.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
            Motor3.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
            Motor4.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
            Motor5.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});

            Motor1.setStepsPerRevolution(2038);
            Motor2.setStepsPerRevolution(2038);
            Motor3.setStepsPerRevolution(2038);
            Motor4.setStepsPerRevolution(2038);
            Motor5.setStepsPerRevolution(2038);

            Scanner in = new Scanner(System.in);
            while (true) {

                System.out.println("Enter Chose");
                int Num = in.nextInt();
                switch (Num) {
                    case 1:
                        Motor1.step(in.nextInt());
                        break;
                    case 2:
                        Motor2.step(in.nextInt());
                        break;
                    case 3:
                        Motor3.step(in.nextInt());
                        break;
                    case 4:
                        Motor4.step(in.nextInt());
                        break;
                    case 5:
                        Motor5.step(in.nextInt());
                        break;
                    default:
                        System.exit(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
