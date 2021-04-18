package com.getxinfo.support;

import com.getxinfo.formats.ChargeParser;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOrder;
import com.igormaznitsa.jbbp.model.JBBPFieldStruct;
import com.igormaznitsa.jbbp.model.JBBPFieldUShort;
import com.igormaznitsa.jbbp.utils.JBBPUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Starter {

    public static void main(String[] args) throws DecoderException, IOException {
        System.out.println("75721E0002000000021002172006061351560400000000FA0400006882030000".length());
        byte[] decoded = JBBPUtils.str2bin("75721E0002000000021002172006061351560400000000FA0400006882030000");
//        ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
//        JBBPBitInputStream bitInputStream = new JBBPBitInputStream(bis, JBBPBitOrder.LSB0);
//        ChargeParser chargeParser = new ChargeParser();
//        ChargeParser result = chargeParser.read(bitInputStream);
//        System.out.println(result);

        JBBPFieldStruct result =  JBBPParser.prepare("").parse(decoded);
        result.findFieldForNameAndType("start", JBBPFieldUShort.class);
    }

}
