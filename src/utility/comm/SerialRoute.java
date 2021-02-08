/**
 * Jose Jimenez-Olivas 
 * Brandon Cramer
 * Email: jjj023@ucsd.edu
 * 
 *                 University of California, San Diego
 *                           IEEE Micromouse
 *
 * File Name:   SerialRoute.java
 * Description: This class reads data from a serial port and notifies it's
 *              listeners with message recieved
 * Sources of Help: In section "Byte- or Multibyte-Delimited Message Received"
 *        github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
 */

import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;

/**
 * Serial Route object that interacts and connects to hardware ports
 * - Singleton design 
 * @see https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
 */
public class SerialRoute implements SerialPortMessageListener {

  private static final SerialRoute instance = new SerialRoute();
  private ArrayList<ActionListener> listenerList;
  private SerialPort port = null; 

  /*
   * Private constructor for singleton design to create listener list.
   */
  private SerialRoute() {
    listenerList = new ArrayList<ActionListener>();
  }

  /**
   * Gets single instance of the serial route object.
   * @return A Serial Route object.
   */
  public static SerialRoute getInstance() {
    return instance;
  }

  /**
   * Transmits bytes through serial port.
   * @param buffer byte buffer to be sent through serial port.
   * @param bytesToWrite number of bytes in byte buffer to send.
   * @return Number of bytes successfully written otherwise return -1 for error.
   */
  public int writeBytes( byte[] buffer, long bytesToWrite ) {
    if( port == null ) return -1;
    return port.writeBytes(buffer, bytesToWrite);
  }

  /**
   * Transmits message through port with implicit append of delimeter.
   * @param message String message to be sent.
   * @return Number of bytes successfully written otherwise return -1 for error.
   */
  public int sendMessage( String message ) {
    String delimeter = new String(getMessageDelimiter(), StandardCharsets.UTF_8);
    message = message + delimeter;
    return writeBytes(message.getBytes(), message.length());
  }

  /**
   * Connects to a specific port by string name.
   * @param selectedPortName User friendly port name.
   * @return True upon success, false otherwise.
   */
  public boolean connectTo( String selectedPortName ) {
    int index = 0;
    for( String portName : getPortList() ) {
      /* all bindable ports */
      if( selectedPortName.equals(portName) ) {
	      /* connect to port and listen to serial data asynchronously */
        disconnect();
        port = SerialPort.getCommPorts()[ index ];
	      port.openPort();
	      port.addDataListener( this );
        return true;
      }
      index++;
    }
    return false;
  }

  /**
   * Disconnects port from being open.
   * @return Nothing.
   */
  public void disconnect() {
    if( port != null && port.isOpen() ) {
      /* clean-up previous port connection */
      port.removeDataListener();
      port.closePort();
    }
  }

  /**
   * Gets list of user friendly port names.
   * @return List of port names.
   */
  public Vector<String> getPortList() {
    SerialPort commPortList[] = SerialPort.getCommPorts();
    Vector<String> portList = new Vector<String>();

    for( SerialPort commPort : commPortList ) {
      /* name port as described by the host system */
      portList.add( commPort.getSystemPortName() );
    }
    return portList;
  }
 
  /**
   * Notify all listeners that a message was recieved.
   * @param evt Action event - likely a SeroualRouteEvent.
   * @return Nothing.
   */
  private void fireActionPerformed( ActionEvent evt ) {
    for( ActionListener listener : listenerList ) {
      /* notify all action listeners of new event */
      listener.actionPerformed( evt );
    }
  }

  /**
   * Adds new action listener for complete data messages from serial port.
   * @param listener Action listener object that wants to asynchronous listen
   *                 to message events.
   * @return Nothing.
   */
  public void addActionListener( ActionListener listener ) {
    listenerList.add( listener ); 
  }

  /**
   * Removes an action listener.
   * @param listener listener object that was previosly listening.
   * @return Nothing.
   */
  public void removeActionListener( ActionListener listener ) {
    listenerList.remove( listener );
  }


  /**
   * Serial Port dependency that checks if it should trigger an event
   * based on delimiting data.
   * @see https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
   * @return True because our Serial route data is expecting a delimeter.
   */
  @Override
  public boolean delimiterIndicatesEndOfMessage() { 
    return true; 
  }

  /**
   * Notify listener of message recieved on serial port.
   * @see https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
   * @param event Serial port event that is converted to a SerialRoute event.
   * @return Nothing.
   */
  @Override
  public void serialEvent( SerialPortEvent event ) {
    String message = "";
    for( byte character : event.getReceivedData() ) {
      /* convert byte data to character data */
      if( !isInDelimiter(character) ) {
        /* character is not in message delimiter */
        message += (char) character;
      }
    }
    /* fire new event for new message detected */
    fireActionPerformed( new SerialRouteEvent(this, message) );
  }
 
  /**
   * Signify that events are triggered from reading data.
   * @see https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
   * @return Serial port constant that signifies that events should be triggered from 
   *         data recieved. 
   */
  @Override
  public int getListeningEvents() { 
    return SerialPort.LISTENING_EVENT_DATA_RECEIVED; 
  }

  /**
   * Getter for the delimiter.
   * @see https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
   * @return Byte array that will be treated as the delimiter.
   */
  @Override
  public byte[] getMessageDelimiter() { 
    return new byte[] { '\r', '\n' }; 
  }

  /**
   * Check is the character is in the delimiter.
   * @param character byte of data in interest.
   * @return True if character is in delimiter, false otherwise.
   */
  public boolean isInDelimiter( byte character ) {
    for( byte delimiter : getMessageDelimiter() ) {
      if( character == delimiter ) {
        return true;
      }
    }
    return false;
  }
 
  /**
   * Getter method to recieve the number of ports available on the machine.
   * @return Total number ports available.
   */
  public int getAvailablePortCount() {
    return getPortList().size();
  }

  /**
   * Gets system name of connected port.
   * @return System port name if port is connected, otherwise null.
   */
  public String getConnectedPortName() {
    return (port == null) ? null : port.getSystemPortName();
  }
  
}
