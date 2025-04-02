package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestInputUtilReadInt {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // 重定向标准输出以便测试
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // 恢复标准输入输出
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testReadIntValidInput() {
        // 设置模拟输入
        String input = "42\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // 调用被测试方法
        int result = InputUtil.readInt("Enter number: ");
        
        // 验证提示信息
        assertEquals("Enter number: ", outContent.toString());
        
        // 验证返回值
        assertEquals(42, result);
    }
} 