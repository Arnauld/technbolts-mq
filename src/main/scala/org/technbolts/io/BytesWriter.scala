package org.technbolts.io

/**
 *
 *
 */
trait BytesWriter {

  /**
   * Writes the specified byte a 1-byte value.
   */
  def writeByte(v:Byte):BytesWriter

  /**
   * Writes the specified byte (the low eight bits of the argument.
   * a 1-byte value.
   */
  def writeByte(v:Int):BytesWriter

  /**
   * Writes the specified byte (the low eight bits of the argument.
   * a 1-byte value.
   */
  def writeByte(v:Long):BytesWriter = writeByte(v.asInstanceOf[Int])

  /**
   * Writes <code>len</code> bytes from the specified byte array
   * starting at offset <code>off</code> to the underlying output.
   */
  def writeBytes(v:Array[Byte], offset:Int, len:Int):BytesWriter

  /**
   * Writes all bytes from the specified byte array to the underlying output.
   */
  def writeBytes(v:Array[Byte]):BytesWriter = {
    writeBytes(v,0,v.length)
  }

  /**
   * Writes a <code>boolean</code> to the underlying output as
   * a 1-byte value. The value <code>true</code> is written out as the
   * value <code>(byte)1</code>; the value <code>false</code> is
   * written out as the value <code>(byte)0</code>.
   */
  def writeBoolean(v:Boolean):BytesWriter = {
    writeByte(if(v) 1 else 0 )
  }

  /**
   * Writes a <code>short</code> to the underlying output as two
   * bytes, high byte first.
   */
  def writeShort(v:Short):BytesWriter = {
    writeByte((v >>> 8) & 0xFF)
    writeByte((v >>> 0) & 0xFF)
  }

  /**
   * Writes a <code>char</code> to the underlying output as a
   * 2-byte value, high byte first.
   */
  def writeChar(v:Char):BytesWriter = {
    writeByte((v >>> 8) & 0xFF)
    writeByte((v >>> 0) & 0xFF)
  }

  /**
   * Writes an <code>int</code> to the underlying output as four
   * bytes, high byte first.
   */
  def writeInt(v:Int):BytesWriter = {
    writeByte((v >>> 24) & 0xFF)
    writeByte((v >>> 16) & 0xFF)
    writeByte((v >>>  8) & 0xFF)
    writeByte((v >>>  0) & 0xFF)
  }

  /**
   * Writes a <code>long</code> to the underlying output as eight
   * bytes, high byte first.
   */
  def writeLong(v:Long):BytesWriter = {
    writeInt((v >>> 32).asInstanceOf[Int])
    writeInt((v >>>  0).asInstanceOf[Int])
    /*
    writeByte((v >>> 56) & 0xFF)
    writeByte((v >>> 48) & 0xFF)
    writeByte((v >>> 40) & 0xFF)
    writeByte((v >>> 32) & 0xFF)
    writeByte((v >>> 24) & 0xFF)
    writeByte((v >>> 16) & 0xFF)
    writeByte((v >>> 8) & 0xFF)
    writeByte((v >>> 0) & 0xFF)
    */
  }

  /**
   * Converts the float argument to an <code>int</code> using the
   * <code>floatToIntBits</code> method in class <code>Float</code>,
   * and then writes that <code>int</code> value to the underlying
   * output as a 4-byte quantity, high byte first.
   */
  def writeFloat(v:Float):BytesWriter = {
    writeInt(java.lang.Float.floatToIntBits(v))
  }

  /**
   * Converts the double argument to a <code>long</code> using the
   * <code>doubleToLongBits</code> method in class <code>Double</code>,
   * and then writes that <code>long</code> value to the underlying
   * output as an 8-byte quantity, high byte first.
   */
  def writeDouble(v:Double):BytesWriter = {
    writeLong(java.lang.Double.doubleToLongBits(v))
  }

  /**
   * First, two bytes are written to out as if by the <code>writeShort</code>
   * method giving the number of bytes to follow.
   * This value is the number of bytes actually written out, not the length
   * of the string.
   * Following the length, each character of the string is output, in sequence,
   * using the UTF-8 encoding for the character.
   */
  def writeUTF8(v:String):BytesWriter = {
    val utf8Bytes = v.getBytes("UTF8")
    writeInt(utf8Bytes.length)
    writeBytes(utf8Bytes)
  }

}