/**
 * Jose Jimenez-Olivas 
 * Brandon Cramer
 * Email: jjj023@ucsd.edu
 * 
 *                 University of California, San Diego
 *                           IEEE Micromouse
 *
 * File Name:   SerialRouteEvent.java
 * Description: This class notifies the serialRoute listeners by firing 
 *              an event asynchronously with the data recieved on a serial 
 *              port.
 * Sources of Help: In section "Byte- or Multibyte-Delimited Message Received"
 *        github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
 */

import java.awt.event.ActionEvent;

/*
 * Class definition of serial message events.
 * This class allows asynchronous multithreading. 
 */
public class SerialRouteEvent extends ActionEvent {
  SerialRoute port;
  String data;

  /**
   * Creates a serial route event - used to notify listeners of string messages
   * fired from a serial port.
   * @param port Serial port used for listening to hardware peripherals.
   * @param data String of data that will be requested from listeners.
   * @return Nothing.
   */
  public SerialRouteEvent( SerialRoute port, String data ) {
    super( port, ActionEvent.ACTION_PERFORMED, "Serial Communication" );
    this.port = port;
    this.data = data;
  }

  /**
   * Gets the message that fired this event.
   * @return character string representation of message.
   */
  public String getReceivedMessage() {
    return data;
  }

  
}
