/**
 * 
 */
package ua.com.make_bet.bean;

/**
 * @author tfedorov
 * 
 */
public class Matche {

  final String group;
  final String leftCommand;
  final String rightCommand;

  public Matche(String group, String leftCommand, String rightCommand) {
    this.group = group;
    this.leftCommand = leftCommand;
    this.rightCommand = rightCommand;
  }

  public String getGroup() {
    return group;
  }

  public String getLeftCommand() {
    return leftCommand;
  }

  public String getRightCommand() {
    return rightCommand;
  }

}
