package net.jonp.armi;

/** Library of conversion functions. */
public class Conversion
{
    private Conversion()
    {
        // Prevent instantiation
    }

    /**
     * Convert a section of a byte array into a long (big-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static long bytesToLong(final byte[] bytes, final int off)
    {
        return ((bytes[off + 0] & 0xffL) << 56 | //
                (bytes[off + 1] & 0xffL) << 48 | //
                (bytes[off + 2] & 0xffL) << 40 | //
                (bytes[off + 3] & 0xffL) << 32 | //
                (bytes[off + 4] & 0xffL) << 24 | //
                (bytes[off + 5] & 0xffL) << 16 | //
                (bytes[off + 6] & 0xffL) << 8 | //
        (bytes[off + 7] & 0xffL) << 0);
    }

    /**
     * Insert a long into an array of bytes, in big-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void longToBytes(final byte[] bytes, final int off, final long value)
    {
        bytes[off + 0] = (byte)((value >> 56) & 0xff);
        bytes[off + 1] = (byte)((value >> 48) & 0xff);
        bytes[off + 2] = (byte)((value >> 40) & 0xff);
        bytes[off + 3] = (byte)((value >> 32) & 0xff);
        bytes[off + 4] = (byte)((value >> 24) & 0xff);
        bytes[off + 5] = (byte)((value >> 16) & 0xff);
        bytes[off + 6] = (byte)((value >> 8) & 0xff);
        bytes[off + 7] = (byte)((value >> 0) & 0xff);
    }

    /**
     * Convert a section of a byte array into a long (little-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static long bytesToLongLE(final byte[] bytes, final int off)
    {
        return ((bytes[off + 0] & 0xffL) << 0 | //
                (bytes[off + 1] & 0xffL) << 8 | //
                (bytes[off + 2] & 0xffL) << 16 | //
                (bytes[off + 3] & 0xffL) << 24 | //
                (bytes[off + 4] & 0xffL) << 32 | //
                (bytes[off + 5] & 0xffL) << 40 | //
                (bytes[off + 6] & 0xffL) << 48 | //
        (bytes[off + 7] & 0xffL) << 56);
    }

    /**
     * Insert a long into an array of bytes, in little-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void longToBytesLE(final byte[] bytes, final int off, final long value)
    {
        bytes[off + 0] = (byte)((value >> 0) & 0xff);
        bytes[off + 1] = (byte)((value >> 8) & 0xff);
        bytes[off + 2] = (byte)((value >> 16) & 0xff);
        bytes[off + 3] = (byte)((value >> 24) & 0xff);
        bytes[off + 4] = (byte)((value >> 32) & 0xff);
        bytes[off + 5] = (byte)((value >> 40) & 0xff);
        bytes[off + 6] = (byte)((value >> 48) & 0xff);
        bytes[off + 7] = (byte)((value >> 56) & 0xff);
    }

    /**
     * Convert a section of a byte array into an int (big-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static int bytesToInt(final byte[] bytes, final int off)
    {
        return ((bytes[off + 0] & 0xff) << 24 | //
                (bytes[off + 1] & 0xff) << 16 | //
                (bytes[off + 2] & 0xff) << 8 | //
        (bytes[off + 3] & 0xff) << 0);
    }

    /**
     * Insert an int into an array of bytes, in big-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void intToBytes(final byte[] bytes, final int off, final int value)
    {
        bytes[off + 0] = (byte)((value >> 24) & 0xff);
        bytes[off + 1] = (byte)((value >> 16) & 0xff);
        bytes[off + 2] = (byte)((value >> 8) & 0xff);
        bytes[off + 3] = (byte)((value >> 0) & 0xff);
    }

    /**
     * Convert a section of a byte array into an int (little-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static int bytesToIntLE(final byte[] bytes, final int off)
    {
        return ((bytes[off + 0] & 0xff) << 0 | //
                (bytes[off + 1] & 0xff) << 8 | //
                (bytes[off + 2] & 0xff) << 16 | //
        (bytes[off + 3] & 0xff) << 24);
    }

    /**
     * Insert an int into an array of bytes, in little-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void intToBytesLE(final byte[] bytes, final int off, final int value)
    {
        bytes[off + 0] = (byte)((value >> 0) & 0xff);
        bytes[off + 1] = (byte)((value >> 8) & 0xff);
        bytes[off + 2] = (byte)((value >> 16) & 0xff);
        bytes[off + 3] = (byte)((value >> 24) & 0xff);
    }

    /**
     * Convert a section of a byte array into a short (big-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static short bytesToShort(final byte[] bytes, final int off)
    {
        return (short)((bytes[off + 0] & 0xff) << 8 | //
        (bytes[off + 1] & 0xff) << 0);
    }

    /**
     * Insert a short into an array of bytes, in big-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void shortToBytes(final byte[] bytes, final int off, final short value)
    {
        bytes[off + 0] = (byte)((value >> 8) & 0xff);
        bytes[off + 1] = (byte)((value >> 0) & 0xff);
    }

    /**
     * Convert a section of a byte array into a short (little-endian).
     * 
     * @param bytes The byte array to read.
     * @param off The offset of the first byte to read.
     * @return The value converted from bytes.
     */
    public static short bytesToShortLE(final byte[] bytes, final int off)
    {
        return (short)((bytes[off + 0] & 0xff) << 0 | //
        (bytes[off + 1] & 0xff) << 8);
    }

    /**
     * Insert a short into an array of bytes, in little-endian byte order.
     * 
     * @param bytes [OUT] The array of bytes to write into.
     * @param off The offset within the array to write the first byte.
     * @param value The value to convert to bytes.
     */
    public static void shortToBytesLE(final byte[] bytes, final int off, final short value)
    {
        bytes[off + 0] = (byte)((value >> 0) & 0xff);
        bytes[off + 1] = (byte)((value >> 8) & 0xff);
    }

    /**
     * Concatenate the elements of an array into a string.
     * 
     * @param array The array whose elements to concatenate.
     * @param join The string to put between the elements of the array.
     * @return The result.
     */
    public static String arrayToString(final Object[] array, final String join)
    {
        return arrayToString(array, 0, array.length, join);
    }

    /**
     * Concatenate the elements of an array slice into a string.
     * 
     * @param array The array.
     * @param off The index of the first element in the array to include.
     * @param len The number of elements to include.
     * @param join The string to put between the included elements of the array.
     * @return The result.
     */
    public static String arrayToString(final Object[] array, final int off, final int len, final String join)
    {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final Object o = array[off + i];
            if (builder.length() > 0) {
                builder.append(join);
            }

            if (o == null) {
                builder.append("(null)");
            }
            else {
                builder.append(o);
            }
        }

        return builder.toString();
    }

    /**
     * Describe an object, which may be <code>null</code> or an arary.
     * 
     * @param object The object.
     * @return A description of the object.
     */
    public static String describe(final Object object)
    {
        if (object instanceof Object[]) {
            return describeArray((Object[])object);
        }
        else {
            return String.format("%s", object);
        }
    }

    /**
     * Describe an array, recursing through sub-arrays as necessary.
     * 
     * @param array The array to describe.
     * @return An intuitive <code>toString()</code> kind of result on the array.
     */
    public static String describeArray(final Object[] array)
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("[");
        boolean first = true;
        for (final Object element : array) {
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }

            buf.append(describe(element));
        }
        buf.append("]");

        return buf.toString();
    }

    /**
     * Update the given string so its initial character is capitalized.
     * 
     * @param s The string to update.
     * @return The updated string, unless it was <code>null</code> or empty, in
     *         which case the original string is returned.
     */
    public static String capFirst(final String s)
    {
        if (null == s || s.isEmpty()) {
            return s;
        }
        else {
            return (s.substring(0, 1).toUpperCase() + s.substring(1));
        }
    }
}
