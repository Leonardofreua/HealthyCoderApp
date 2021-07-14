package com.healthycoderapp;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class BMICalculatorTest {
    private final static String ENVIRONMENT = "dev";

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all unit tests");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all unit tests");
    }

    @Nested
    class IsDietRecommendedTests {
        @ParameterizedTest(name = "weight={0}, height={1}")
        @CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1)
        void should_ReturnTrue_When_DietRecommended(Double coderWeight, Double coderHeight) {
            // given
            double weight = coderWeight;
            double height = coderHeight;

            // when
            boolean recommended = BMICalculator.isDietRecommended(weight, height);

            // then
            assertTrue(recommended);
        }

        @Test
        void should_ReturnFalse_When_DietNotRecommended() {
            // given
            double weight = 50.0;
            double height = 1.92;

            // when
            boolean recommended = BMICalculator.isDietRecommended(weight, height);

            // then
            assertFalse(recommended);
        }

        @Test
        void should_ThrowArithmeticException_When_HeightZero() {
            // given
            double weight = 50.0;
            double height = 0.0;

            // when
            Executable executable = () -> BMICalculator.isDietRecommended(weight, height);

            // then
            assertThrows(ArithmeticException.class, executable);
        }
    }

    @Nested
    @DisplayName("{{}} sample inner class display name")
    class FindCoderWithWorstBMITests {
        @Test
        @DisplayName("{{}} sample method display name")
        void should_ReturnCoderWithWorstBMI_When_CoderListNotEmpty() {
            // given
            List<Coder> coders = new ArrayList<>();
            coders.add(new Coder(1.80, 60.0));
            coders.add(new Coder(1.82, 98.0));
            coders.add(new Coder(1.82, 64.7));

            // when
            var coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

            // then
            assertAll(
                () -> assertEquals(1.82, coderWorstBMI.getHeight()),
                () -> assertEquals(98.0, coderWorstBMI.getWeight())
            );
        }

        @Test
        void should_ReturnCoderWithWorstBMIIn100Ms_When_CoderListHas10000Elements() {
            assumeTrue(BMICalculatorTest.ENVIRONMENT.equals("prod"));

            // given
            List<Coder> coders = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                coders.add(new Coder(1.0 + i, 10.0 + i));
            }

            // when
            Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

            // then
            assertTimeout(Duration.ofMillis(100), executable);
        }

        @Test
        void should_ReturnNullWorstBMICoder_When_CoderListEmpty() {
            // given
            List<Coder> coders = new ArrayList<>();

            // when
            var coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

            // then
            assertNull(coderWorstBMI);
        }
    }

    @Nested
    class GetBMIScoresTests {
        @Test
        void should_ReturnCorrectBMIScoreArray_When_CoderListNotEmpty() {
            // given
            List<Coder> coders = new ArrayList<>();
            coders.add(new Coder(1.80, 60.0));
            coders.add(new Coder(1.82, 98.0));
            coders.add(new Coder(1.82, 64.7));

            double[] expectedScores = {18.52, 29.59, 19.53};

            // when
            var bmiScores = BMICalculator.getBMIScores(coders);

            // then
            assertArrayEquals(expectedScores, bmiScores);
        }
    }
}
