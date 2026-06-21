package filter.ast.builder;

import filter.FilterBaseVisitor;
import filter.FilterParser;
import filter.ast.nodes.CompOp;
import filter.ast.nodes.Expr;
import filter.ast.nodes.Value;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class AstBuilderVisitor extends FilterBaseVisitor<Void> {

  // TODO
  // Stacks für Expr und Value
  private final Deque<Expr> exprStack = new LinkedList<>();
  private final Deque<Value> valueStack = new LinkedList<>();

  // Public entry point
  public Expr translate(FilterParser.QueryContext ctx) {
    // TODO
    if (ctx == null) return null;
    // Stacks säubern
    exprStack.clear();
    valueStack.clear();

    // starte
    visit(ctx);

    // Gebe das oberste Element vom Stack
    return exprStack.isEmpty() ? null : exprStack.pop();
  }

  // query  : expr EOF
  @Override
  public Void visitQuery(FilterParser.QueryContext ctx) {
    // TODO
    if (ctx == null) return null;
    return visit(ctx.expr());
  }

  // expr: orExpr
  @Override
  public Void visitExpr(FilterParser.ExprContext ctx) {
    // TODO
    if (ctx == null) return null;
    return visit(ctx.orExpr());
  }

  // orExpr : andExpr (OR andExpr)*
  @Override
  public Void visitOrExpr(FilterParser.OrExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    // Erste Element der Liste untersuchen
    visit(ctx.andExpr(0));

    // Falls vorhanden die anderen Elemente untersuchen
    for (int i = 1; i < ctx.andExpr().size(); i++) {
      visit(ctx.andExpr(i));
      Expr rechts = exprStack.pop();
      Expr links = exprStack.pop();

      // Erstelle Or-Record
      exprStack.push(new Expr.Or(links, rechts));
    }
    return null;
  }

  // andExpr: notExpr (AND notExpr)*
  @Override
  public Void visitAndExpr(FilterParser.AndExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    visit(ctx.notExpr(0));

    for (int i = 1; i < ctx.notExpr().size(); i++) {
      visit(ctx.notExpr(i));
      Expr rechts = exprStack.pop();
      Expr links = exprStack.pop();

      exprStack.push(new Expr.And(links, rechts));
    }
    return null;
  }

  // notExpr: NOT notExpr | primary
  @Override
  public Void visitNotExpr(FilterParser.NotExprContext ctx) {
    // TODO
    if (ctx == null) return null;

    // ein not ist vorhanden
    if (ctx.NOT() != null) {
      visit(ctx.notExpr());
      Expr inner = exprStack.pop();
      exprStack.push(new Expr.Not(inner));
    }
    // ansonsten zu primary
    else {
      visit(ctx.primary());
    }
    return null;
  }

  // primary: comparison | '(' expr ')'
  @Override
  public Void visitPrimary(FilterParser.PrimaryContext ctx) {
    // TODO
    if (ctx == null) return null;

    // geklammerter Ausdruck z.b (artist == "Beatles") vorhanden
    if (ctx.expr() != null) {
      visit(ctx.expr());
    } else {
      visit(ctx.comparison());
    }
    return null;
  }

  // comparison
  //   : IDENTIFIER op=COMPOP value=literal
  //   | IDENTIFIER IN '(' literalList ')'
  @Override
  public Void visitComparison(FilterParser.ComparisonContext ctx) {
    // TODO
    if (ctx == null) return null;
    String field = ctx.IDENTIFIER().getText();

    // literalList vorhanden
    if (ctx.literalList() != null) {
      visit(ctx.literalList());

      // anzahl der Elemente der Liste ermitteln
      int anzahlWerte = ctx.literalList().literal().size();
      List<Value> values = new ArrayList<>();

      // Werte rückwärts von Stack zurück holen
      for (int i = 0; i < anzahlWerte; i++) {
        // immer auf index 0 setzen um die reihenfolge zu bewahren
        values.add(0, valueStack.pop());
      }

      exprStack.push(new Expr.InList(field, values));
    }
    // normale comparsion
    else {
      visit(ctx.value);

      Value value = valueStack.pop();
      CompOp op = CompOp.fromSymbol((ctx.op.getText()));
      exprStack.push(new Expr.Comparison(field, op, value));
    }
    return null;
  }

  // literalList: literal (',' literal)*
  @Override
  public Void visitLiteralList(FilterParser.LiteralListContext ctx) {
    // TODO
    if (ctx == null) return null;

    for (FilterParser.LiteralContext litCtx : ctx.literal()) {
      visit(litCtx);
    }

    return null;
  }

  // literal: STRING | NUMBER
  @Override
  public Void visitLiteral(FilterParser.LiteralContext ctx) {
    // TODO
    if (ctx == null) return null;

    // literal ist Zahl
    if (ctx.NUMBER() != null) {
      int zahl = Integer.parseInt(ctx.NUMBER().getText());
      valueStack.push(new Value.Num(zahl));
    }
    // literal ist String
    else if (ctx.STRING() != null) {
      String textMitQuotes = ctx.STRING().getText();
      // anführungszeichen entfernen
      String text = textMitQuotes.substring(1, textMitQuotes.length() - 1);

      valueStack.push(new Value.Str(text));
    }

    return null;
  }
}
