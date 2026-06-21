package filter.ast.builder;

import filter.FilterParser;
import filter.ast.nodes.*;
import filter.ast.nodes.Expr;
import filter.ast.nodes.Value;
import java.util.List;

public class AstBuilderPattern {

  // Public entry point
  // query  : expr EOF
  public Expr translate(FilterParser.QueryContext ctx) {
    // TODO
    if (ctx == null || ctx.expr() == null) return null;
    return buildExpr(ctx.expr());
  }

  // expr: orExpr
  private Expr buildExpr(FilterParser.ExprContext ctx) {
    // TODO
    if (ctx == null) return null;
    return buildOrExpr(ctx.orExpr());
  }

  // orExpr : andExpr (OR andExpr)*
  private Expr buildOrExpr(FilterParser.OrExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    Expr result = buildAndExpr(ctx.andExpr(0));

    for (int i = 1; i < ctx.andExpr().size(); i++) {
      Expr right = buildAndExpr(ctx.andExpr(i));
      result = new Expr.Or(result, right);
    }

    return result;
  }

  // andExpr: notExpr (AND notExpr)*
  private Expr buildAndExpr(FilterParser.AndExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    Expr result = buildNotExpr(ctx.notExpr(0));

    for (int i = 1; i < ctx.notExpr().size(); i++) {
      Expr right = buildNotExpr(ctx.notExpr(i));
      result = new Expr.And(result, right);
    }

    return result;
  }

  // notExpr: NOT notExpr | primary
  private Expr buildNotExpr(FilterParser.NotExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    if (ctx.NOT() != null) {
      Expr inner = buildNotExpr(ctx.notExpr());
      return new Expr.Not(inner);
    } else {
      return buildPrimary(ctx.primary());
    }
  }

  // primary: comparison | '(' expr ')'
  private Expr buildPrimary(FilterParser.PrimaryContext ctx) {
    // TODO
    if (ctx == null) return null;

    if (ctx.expr() != null) {
      return buildExpr(ctx.expr());
    } else {
      return buildComparison(ctx.comparison());
    }
  }

  // comparison
  //   : IDENTIFIER op=COMPOP value=literal
  //   | IDENTIFIER IN '(' literalList ')'
  private Expr buildComparison(FilterParser.ComparisonContext ctx) {
    // TODO
    if (ctx == null) return null;

    String field = ctx.IDENTIFIER().getText();

    if (ctx.literalList() != null) {
      List<Value> values = buildLiteralList(ctx.literalList());
      return new Expr.InList(field, values);
    } else {
      String operatorSymbol = ctx.op.getText();
      CompOp op = CompOp.fromSymbol(operatorSymbol);
      Value value = buildLiteral(ctx.value);
      return new Expr.Comparison(field, op, value);
    }
  }

  // literalList: literal (',' literal)*
  private List<Value> buildLiteralList(FilterParser.LiteralListContext ctx) {
    // TODO
    if (ctx == null) return List.of();

    List<Value> values = new java.util.ArrayList<>();
    for (FilterParser.LiteralContext literalCtx : ctx.literal()) {
      Value astValue = buildLiteral(literalCtx);
      if (astValue != null) {
        values.add(astValue);
      }
    }
    return values;
  }

  // literal: STRING | NUMBER
  private Value buildLiteral(FilterParser.LiteralContext ctx) {
    // TODO
    if (ctx == null) return null;

    // Zahl erkannt?
    if (ctx.NUMBER() != null) {
      // in String wandeln
      String zahlAlsText = ctx.NUMBER().getText(); // "5"
      // in int speichern
      int zahl = Integer.parseInt(zahlAlsText); // 5
      // int im num Record returnen
      return new Value.Num(zahl);
    }

    // String erkannt?
    if (ctx.STRING() != null) {
      String textMitAnfuehrungszeichen = ctx.STRING().getText();
      // Anführungszeichen absschneiden
      String text = textMitAnfuehrungszeichen.substring(1, textMitAnfuehrungszeichen.length() - 1);
      // String in String Record returnen
      return new Value.Str(text);
    }

    return null;
  }
}
