package com.getxinfo.support;

import com.igormaznitsa.jbbp.JBBPCustomFieldTypeProcessor;
import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOrder;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import com.igormaznitsa.jbbp.model.JBBPFieldArrayLong;
import com.igormaznitsa.jbbp.model.JBBPFieldLong;

import java.io.IOException;

public class PackedBCDCustomField implements JBBPCustomFieldTypeProcessor {

    private static final String[] types = new String[]{"bcd", "sbcd"};

    public static long readValueFromPackedDecimal(final JBBPBitInputStream in, final int len,
                                                  final boolean signed) throws IOException {
        final byte[] data = in.readByteArray(len);

        StringBuilder digitStr = new StringBuilder();
        for (int i = 0; i < len * 2; i++) {
            byte currentByte = data[i / 2];
            byte digit = (i % 2 == 0) ? (byte) ((currentByte & 0xff) >>> 4) : (byte) (currentByte & 0x0f);
            if (digit < 10) {
                digitStr.append(digit);
            }
        }

        if (signed) {
            byte sign = (byte) (data[len - 1] & 0x0f);
            if (sign == 0x0b || sign == 0x0d) {
                digitStr.insert(0, '-');
            }
        }

        return Long.parseLong(digitStr.toString());
    }

    @Override
    public String[] getCustomFieldTypes() {
        return types;
    }

    @Override
    public boolean isAllowed(final JBBPFieldTypeParameterContainer fieldType, final String fieldName,
                             final int extraData, final boolean isArray) {
        if (fieldType.getByteOrder() == JBBPByteOrder.LITTLE_ENDIAN) {
            System.err
                    .println("Packed Decimal does not support little endian...using big endian instead");
            return false;
        }

        return extraData > 0 && extraData < 15;
    }

    @Override
    public JBBPAbstractField readCustomFieldType(final JBBPBitInputStream in,
                                                 final JBBPBitOrder bitOrder, final int parserFlags,
                                                 final JBBPFieldTypeParameterContainer customTypeFieldInfo,
                                                 JBBPNamedFieldInfo fieldName, int extraData,
                                                 boolean readWholeStream, int arrayLength) throws IOException {
        final boolean signed = "sbcd".equals(customTypeFieldInfo.getTypeName());

        if (readWholeStream) {
            throw new UnsupportedOperationException("Whole stream reading unsupported");
        } else {
            if (arrayLength <= 0) {
                return new JBBPFieldLong(fieldName, readValueFromPackedDecimal(in, extraData, signed));
            } else {
                final long[] result = new long[arrayLength];
                for (int i = 0; i < arrayLength; i++) {
                    result[i] = readValueFromPackedDecimal(in, extraData, signed);
                }
                return new JBBPFieldArrayLong(fieldName, result);
            }
        }
    }
}
