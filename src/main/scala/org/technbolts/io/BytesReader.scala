package org.technbolts.io

import java.io.EOFException

/**
 *
 *
 */
trait BytesReader {

  /**
   * Reads the next byte of data from the input stream. The value byte is
   * returned as an <code>int</code> in the range <code>0</code> to
   * <code>255</code>. If no byte is available because the end of the stream
   * has been reached, the value <code>-1</code> is returned. This method
   * blocks until input data is available, the end of the stream is detected,
   * or an exception is thrown.
   */
  def read():Int

  /**
   *  Reads up to <code>len</code> bytes of data from the contained
   * input stream into an array of bytes.  An attempt is made to read
   * as many as <code>len</code> bytes, but a smaller number may be read,
   * possibly zero. The number of bytes actually read is returned as an
   * integer.
   *
   * <p> If <code>len</code> is zero, then no bytes are read and
   * <code>0</code> is returned; otherwise, there is an attempt to read at
   * least one byte. If no byte is available because the stream is at end of
   * file, the value <code>-1</code> is returned; otherwise, at least one
   * byte is read and stored into <code>b</code>.
   *
   * <p> The first byte read is stored into element <code>b[off]</code>, the
   * next one into <code>b[off+1]</code>, and so on. The number of bytes read
   * is, at most, equal to <code>len</code>. Let <i>k</i> be the number of
   * bytes actually read; these bytes will be stored in elements
   * <code>b[off]</code> through <code>b[off+</code><i>k</i><code>-1]</code>,
   * leaving elements <code>b[off+</code><i>k</i><code>]</code> through
   * <code>b[off+len-1]</code> unaffected.
   *
   * <p> In every case, elements <code>b[0]</code> through
   * <code>b[off]</code> and elements <code>b[off+len]</code> through
   * <code>b[b.length-1]</code> are unaffected.
   */
  def readBytes(in:Array[Byte], off: Int, len: Int): Int

  /**
   * Reads some number of bytes from the contained input stream and
   * stores them into the buffer array <code>b</code>. The number of
   * bytes actually read is returned as an integer.
   */
  def readBytes(bytes:Array[Byte]):Int = {
    readBytes(bytes, 0, bytes.length)
  }

  /**
   * Reads <code>len</code> bytes from input.
   * <p>
   * This method blocks until one of the following conditions
   * occurs:<p>
   * <ul>
   *  <li><code>len</code> bytes
   *    of input data are available, in which case
   *    a normal return is made.</li>
   *  <li>End of file
   *    is detected, in which case an <code>EOFException</code>
   *    is thrown.</li>
   * </ul>
   */
  def readFully(bytes: Array[Byte], off: Int, len: Int):Array[Byte] = {
    if (len < 0) throw new IndexOutOfBoundsException
    var n: Int = 0
    while (n < len) {
      var count: Int = readBytes(bytes, off + n, len - n)
      if (count < 0) throw new EOFException
      n += count
    }
    bytes
  }

  /**
   *
   */
  def readFully(bytes: Array[Byte]):Array[Byte] = {
    readFully(bytes, 0, bytes.length)
    bytes
  }

  /**
   *
   */
  def readOrEOF():Int = {
    val v = read
    if(v<0) throw new EOFException
    v
  }

  /**
   *  Bytes for this operation are read from the contained input.
   */
  def readBoolean():Boolean = {
    (readOrEOF()!=0)
  }

  /**
   *
   */
  def readByte():Byte = {
    readOrEOF().asInstanceOf[Byte]
  }

  /**
   *
   */
  def readShort():Short = {
    val v1 = readOrEOF()
    val v2 = readOrEOF()
    ((v1<<8) + (v2<<0)).asInstanceOf[Short]   
  }

  /**
   *
   */
  def readChar():Char = {
    val v1 = readOrEOF()
    val v2 = readOrEOF()
    ((v1<<8) + (v2<<0)).asInstanceOf[Char]
  }

  /**
   *
   */
  def readInt():Int = {
    val v1 = readOrEOF
    val v2 = readOrEOF
    val v3 = readOrEOF
    val v4 = readOrEOF
    ((v1 << 24) + (v2 << 16) + (v3 << 8) + (v4 << 0))
  }

  /**
   *
   */
  def readLong():Long = {
    val v1 = readInt.asInstanceOf[Long]
    val v2 = readInt.asInstanceOf[Long]
    ((v1 << 32) + (v2 <<0))
  }

  /**
   *
   */
  def readFloat():Float = {
    java.lang.Float.intBitsToFloat(readInt)
  }

  /**
   *
   */
  def readDouble():Double = {
    java.lang.Double.longBitsToDouble(readLong)
  }

  /**
   *
   */
  def readUTF8():String = {
    val len = readInt
    val bytes = readFully(new Array[Byte](len))
    new String(bytes,"UTF8")
  }
}