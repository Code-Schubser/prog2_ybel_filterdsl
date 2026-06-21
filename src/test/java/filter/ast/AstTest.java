package filter.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import filter.ast.builder.AstBuilderPattern;
import filter.ast.builder.AstBuilderVisitor;
import filter.ast.builder.AstBuilders;
import filter.ast.nodes.CompOp;
import filter.ast.nodes.Expr;
import filter.ast.nodes.Value;
import org.junit.jupiter.api.Test;

public class AstTest {

  // Tests AstBuilderVisitor
  // Zahlen vergleich
  @Test
  void testEinfacherVergleich() {
    String text = "year == 2020";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderVisitor()::translate);
    Expr erwartung = new Expr.Comparison("year", CompOp.EQ, new Value.Num(2020));
    assertEquals(erwartung, ergebnis);
  }

  // Text vergleich mit Anführungszeichen
  @Test
  void testTextVergleich() {
    String text = "artist == \"Beatles\"";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderVisitor()::translate);
    Expr erwartung = new Expr.Comparison("artist", CompOp.EQ, new Value.Str("Beatles"));
    assertEquals(erwartung, ergebnis);
  }

  // Und-Verknüpfung mit zwei literals
  @Test
  void testAndVerknuepfung() {
    String text = "year == 2020 and artist == \"Beatles\"";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderVisitor()::translate);

    Expr erwartung =
        new Expr.And(
            new Expr.Comparison("year", CompOp.EQ, new Value.Num(2020)),
            new Expr.Comparison("artist", CompOp.EQ, new Value.Str("Beatles")));
    assertEquals(erwartung, ergebnis);
  }

  // Tests AstBuilderPattern
  // Zahlen vergleich
  @Test
  void testEinfacherVergleich2() {
    String text = "year == 2020";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderPattern()::translate);
    Expr erwartung = new Expr.Comparison("year", CompOp.EQ, new Value.Num(2020));
    assertEquals(erwartung, ergebnis);
  }

  // Text vergleich mit Anführungszeichen
  @Test
  void testTextVergleich2() {
    String text = "artist == \"Beatles\"";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderPattern()::translate);
    Expr erwartung = new Expr.Comparison("artist", CompOp.EQ, new Value.Str("Beatles"));
    assertEquals(erwartung, ergebnis);
  }

  // Und-Verknüpfung mit zwei literals
  @Test
  void testAndVerknuepfung2() {
    String text = "year == 2020 and artist == \"Beatles\"";
    Expr ergebnis = AstBuilders.fromQuery(text, new AstBuilderPattern()::translate);

    Expr erwartung =
        new Expr.And(
            new Expr.Comparison("year", CompOp.EQ, new Value.Num(2020)),
            new Expr.Comparison("artist", CompOp.EQ, new Value.Str("Beatles")));
    assertEquals(erwartung, ergebnis);
  }
}
