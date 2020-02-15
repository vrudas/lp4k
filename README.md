LP4K - A Launchpad API for Java
====
![](https://github.com/vrudas/lp4k/workflows/build/badge.svg)
![](https://github.com/vrudas/lp4k/workflows/sonarqube/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=vrudas_lp4k&metric=alert_status)](https://sonarcloud.io/dashboard?id=vrudas_lp4k)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=vrudas_lp4k&metric=ncloc)](https://sonarcloud.io/dashboard?id=vrudas_lp4k)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=vrudas_lp4k&metric=coverage)](https://sonarcloud.io/dashboard?id=vrudas_lp4k)

Launchpad is a midi device built by Novation, used to drive computer-assisted music creation applications such as Ableton Live.

The device is a square board with a 8x8 pads grid, surrounded by round command buttons. Pads and buttons can be lit in various tints of yellow, red and green.
This makes the Launchpad an interesting general-purpose I/O device, where a user can press pads and buttons to send commands to an application, and provide feedback with its 8x8 matrix of colored leds.

![Launchpad](launchpad.jpg)

LP4K is a lightweight Java API (initially based on sources of LP4J project on https://github.com/OlivierCroisier/LP4J) allowing any Java application to interact with a Launchpad Mini MK3.
It also provides a embedded, web-based emulator to allow developers to design and test applications should they not have access to a physical device.

The API is split in 3 submodules :
- lp4k-api : an abstract, high-level API defining how to interact with a Launchpad (LaunchpadClient / LaunchpadListener)
- lp4k-midi : a MIDI implementation of the API, suitable to interact with a physical device.
- lp4k-emulator : a web-based emulator, using SVG and websockets.

How to use LP4K
====

From a developper's perspective, all interaction with LP4K are done through the high-level API.

First, you need to get a reference to a Launchpad :

```java
    // Physical device (with auto-detected ports configuration)
    Launchpad launchpad = new MidiLaunchpad(MidiDeviceConfiguration.autodetect());

    // Or start the emulator on http://localhost:9000
    Launchpad launchpad = new EmulatorLaunchpad(9000);
```

From this Launchpad instance, you can :
- retrieve a LaunchpadClient, used to send commands TO the device or emulator (mostly to turn on/off the pads or buttons lights),
- set up a LaunchpadListener to react to events.

```java
     LaunchpadClient client = launchpad.getClient();
     launchpad.setListener(new MyListener());
```

A very simple debugging listener can look like this :

```java
    public static class MyListener extends LaunchpadListenerAdapter {

        @Override
        public void onPadPressed(Pad pad, long timestamp) {
            System.out.println("Pad pressed : "+pad);
        }

    }
```

A more complex example
====

Listeners can be more complex.
For example, the following Listener sends commands back to the Launchpad to put a yellow light under pads when they are pressed, lighting them off when they are released.

Please also note the use of a CountDownLatch to wait for the user to press the "STOP" button before exiting the application.

```java
public class Example {

    private static CountDownLatch stop = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        Launchpad launchpad = new EmulatorLaunchpad(9000);
        LaunchpadClient client = launchpad.getClient();

        MyListener myListener = new MyListener(client);
        launchpad.setListener(myListener);

        // Set a red light under the STOP button
        client.reset();
        client.setButtonLight(Button.STOP, Color.RED, BackBufferOperation.NONE);

        stop.await();
        client.reset();
        launchpad.close();
    }

    public static class MyListener extends LaunchpadListenerAdapter {

        private final LaunchpadClient client;

        public MyListener(LaunchpadClient client) {
            this.client = client;
        }

        @Override
        public void onPadPressed(Pad pad, long timestamp) {
            client.setPadLight(pad, Color.YELLOW, BackBufferOperation.NONE);
        }

        @Override
        public void onPadReleased(Pad pad, long timestamp) {
            client.setPadLight(pad, Color.BLACK, BackBufferOperation.NONE);
        }

        @Override
        public void onButtonReleased(Button button, long timestamp) {
            client.setButtonLight(button, Color.BLACK, BackBufferOperation.NONE);
            switch (button) {
                case STOP:
                    stop.countDown();
                    break;
            }
        }
    }

}
```
