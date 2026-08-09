/*
 * Copyright (C) 2026 Navdeep Singh Sidhu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.co.s13.sips.run.tools;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parsing the Java people actually write.
 *
 * <p>SIPS builds and runs on JDK 21, but parsed submitted source with
 * JavaParser 3.5.2 — a 2017 release that predates records, sealed types, switch
 * expressions, text blocks and enhanced instanceof. A user on a current JDK
 * would have had their job rejected at the parse step for using ordinary modern
 * syntax, with nothing to indicate why.
 *
 * <p>Each case below fails to parse on 3.5.2.
 */
class ModernJavaParsingTest {

    @BeforeAll
    static void useTheLanguageLevelWeBuildFor() {
        StaticJavaParser.getParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        // records — Java 16
        "public record Point(int x, int y) {}",
        // sealed types — Java 17
        "public sealed interface Shape permits Circle {} final class Circle implements Shape {}",
        // enhanced instanceof — Java 16
        "class A { void f(Object o) { if (o instanceof String s) { System.out.println(s); } } }",
        // var — Java 10
        "class A { void f() { var xs = new java.util.ArrayList<String>(); } }"
    })
    void parsesModernDeclarations(String source) {
        assertDoesNotThrow(() -> {
            CompilationUnit unit = StaticJavaParser.parse(source);
            assertNotNull(unit);
        }, "failed to parse: " + source);
    }

    @Test
    void parsesSwitchExpressions() {
        // Java 14. Common in exactly the numeric code SIPS distributes.
        String source = """
                        class A {
                            int f(int day) {
                                return switch (day) {
                                    case 1, 2 -> 10;
                                    default -> 0;
                                };
                            }
                        }
                        """;
        assertNotNull(StaticJavaParser.parse(source));
    }

    @Test
    void parsesTextBlocks() {
        // Java 15.
        String source = "class A { String s = \"\"\"\n    hello\n    \"\"\"; }";
        assertNotNull(StaticJavaParser.parse(source));
    }

    /**
     * The shape SIPS actually has to parse: a marked parallel loop, written in
     * modern Java.
     */
    @Test
    void parsesAParallelLoopInModernSource() {
        String source = """
                        import in.co.s13.sips.lib.SIPS;

                        public class Search {
                            record Hit(long index, String value) {}

                            public static void main(String[] args) {
                                SIPS sim = new SIPS("Search");
                                var hits = new java.util.ArrayList<Hit>();
                                sim.simulateSection();
                                int count = 1000;
                                sim.saveValues("" + count);
                                sim.endSimulateSection();

                                sim.parallelFor();
                                for (int i = 0; i < count; i++) {
                                    if (i % 7 == 0) {
                                        continue;
                                    }
                                    hits.add(new Hit(i, switch (i % 3) {
                                        case 0 -> "zero";
                                        default -> "other";
                                    }));
                                }
                                sim.endParallelFor();
                            }
                        }
                        """;
        CompilationUnit unit = StaticJavaParser.parse(source);

        assertNotNull(unit);
        assertTrue(unit.toString().contains("parallelFor"),
                "the marker must survive parsing, since the AST pass looks for it");
    }

    @Test
    void stillParsesTheOlderSyntaxSamplesAreWrittenIn() {
        // The upgrade must not break the existing samples.
        String source = """
                        class MatMul {
                            public static void main(String args[]) {
                                double a[][] = new double[10][10];
                                for (int i = 0; i < 10; i++) {
                                    a[i][i] = 1.0;
                                }
                            }
                        }
                        """;
        assertNotNull(StaticJavaParser.parse(source));
    }
}
