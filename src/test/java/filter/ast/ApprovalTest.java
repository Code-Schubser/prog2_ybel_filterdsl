package filter.ast;

import filter.ast.builder.AstBuilderPattern;
import filter.ast.builder.AstBuilderVisitor;
import filter.ast.builder.AstBuilders;
import filter.ast.nodes.Expr;
import filter.ast.printer.AstPrinter;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

public class ApprovalTest {
  // TODO
  @Test
  void testKomplexeQueryPatternBuilder() {
    String query = "genre in (\"rock\", \"jazz\") or year <= 1990 and not artist == \"Beatles\"";
    Expr ast = AstBuilders.fromQuery(query, new AstBuilderPattern()::translate);
    String astString = AstPrinter.toString(ast);
    Approvals.verify(astString);
  }

  @Test
  void testKomplexeQueryVisitorBuilder() {
    String query = "genre in (\"rock\", \"jazz\") or year <= 1990 and not artist == \"Beatles\"";
    Expr ast = AstBuilders.fromQuery(query, new AstBuilderVisitor()::translate);
    String astString = AstPrinter.toString(ast);
    Approvals.verify(astString);
  }

  @Test
  void testSimplifyDoppeltesNot() {
    String query = "not not artist == \"Beatles\"";
    Expr ast = AstBuilders.fromQuery(query, new AstBuilderPattern()::translate);
    String astString = AstPrinter.toString(ast);
    Approvals.verify(astString);
  }
}
