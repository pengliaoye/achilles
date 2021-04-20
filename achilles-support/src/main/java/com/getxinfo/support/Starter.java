package com.getxinfo.support;

import com.getxinfo.formats.ChargeParser;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOrder;
import com.igormaznitsa.jbbp.model.JBBPFieldArrayByte;
import com.igormaznitsa.jbbp.model.JBBPFieldArrayStruct;
import com.igormaznitsa.jbbp.model.JBBPFieldStruct;
import com.igormaznitsa.jbbp.model.JBBPFieldUShort;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.PureJavaCrc32;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Starter {

    public static void main(String[] args) throws DecoderException, IOException {
        String data = ("75 72 0B 01 02 00 00 00 06 10 02 17 00 01 01 01 " +
                "01 02 01 01 00 03 01 01 70 04 01 14 30 30 30 30 " +
                "30 30 30 30 42 45 45 36 38 31 41 34 00 CC CC CC " +
                "05 01 11 30 36 31 34 34 31 35 36 00 00 00 00 00 " +
                "00 00 00 00 06 01 04 62 4B 00 00 07 01 04 DB 03 " +
                "00 00 08 01 04 61 00 00 00 09 01 04 64 00 00 00 " +
                "0A 01 04 01 00 00 00 0B 01 04 E9 03 00 00 0C 01 " +
                "04 00 00 00 00 0D 01 04 00 00 00 00 0E 01 04 B0 " +
                "04 00 00 0F 01 01 00 10 01 01 00 11 01 01 00 12 " +
                "01 01 00 13 01 07 17 20 06 06 14 41 47 14 01 07 " +
                "17 20 06 06 14 41 57 15 01 04 10 00 00 00 16 01 " +
                "04 02 00 00 00 17 01 50 01 00 00 00 01 00 00 00 " +
                "02 00 00 00 02 00 00 00 03 00 00 00 03 00 00 00 " +
                "04 00 00 00 04 00 00 00 05 00 00 00 05 00 00 00 " +
                "06 00 00 00 06 00 00 00 07 00 00 00 07 00 00 00 " +
                "08 00 00 00 08 00 00 00 09 00 00 00 09 00 00 00 " +
                "0A 00 00 00 0A 00 00 00 68 7C 11 00 00").replaceAll(" ", "");
        byte[] decoded = Hex.decodeHex(data);
//        byte[] decoded = JBBPUtils.str2bin(data);
        ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
        JBBPBitInputStream bitInputStream = new JBBPBitInputStream(bis, JBBPBitOrder.LSB0);
        ChargeParser chargeParser = new ChargeParser();
        chargeParser.read(bitInputStream);

        PureJavaCrc32 crc = new PureJavaCrc32();
        System.out.println(Hex.encodeHex(Arrays.copyOfRange(decoded, 0, decoded.length - 4)));
        crc.update(Arrays.copyOfRange(decoded, 0, decoded.length - 4));
        long crcReal = crc.getValue();
        long crcExpect = Integer.toUnsignedLong(chargeParser.crc);

        System.out.println(chargeParser);

        System.out.println(Hex.encodeHex(chargeParser.data));
//        JBBPFieldStruct result = JBBPParser.prepare("bcd:7 time;byte pile_type;int pile_info;int version;", new PackedBCDCustomField()).parse(chargeParser.data);
//        result.findFieldForNameAndType("start", JBBPFieldArrayUByte.class);
//        int version = result.findFieldForNameAndType("version", JBBPFieldInt.class).getAsInt();
//        long ver = Integer.toUnsignedLong(version);
//        System.out.println(version);
//        System.out.println(result);

        JBBPFieldStruct result = JBBPParser.prepare("ushort sections_len; sections [17] {ushort key; byte datalen; byte [datalen] val;}").parse(chargeParser.data);
        JBBPFieldArrayStruct sections = result.findFieldForNameAndType("sections", JBBPFieldArrayStruct.class);
        Map<Integer, String> unitMap = new HashMap<>();
        unitMap.put(0x0101, "chargingMethod");
        unitMap.put(0x0102, "chargingMode");
        unitMap.put(0x0401, "chargingMode");
        for (int i = 0; i < sections.size(); i++) {
            JBBPFieldStruct fieldStruct = (JBBPFieldStruct) sections.getElementAt(i);
            int key = fieldStruct.findFieldForNameAndType("key", JBBPFieldUShort.class).getAsInt();
            byte[] bytes = fieldStruct.findFieldForNameAndType("val", JBBPFieldArrayByte.class).getArray();
            String keyName = unitMap.get(key);
            if (keyName != null) {
                String val = Hex.encodeHexString(bytes);
                System.out.println(keyName + "=" + val);
            }
        }
        System.out.println(result);
    }

}
