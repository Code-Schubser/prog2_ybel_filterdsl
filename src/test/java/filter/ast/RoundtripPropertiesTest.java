package filter.ast;

import filter.ast.builder.AstBuilderPattern;
import filter.ast.builder.AstBuilderVisitor;
import filter.ast.builder.AstBuilders;
import filter.ast.nodes.Expr;
import filter.ast.printer.AstPrinter;
import net.jqwik.api.*;

public class RoundtripPropertiesTest {

  // TODO
  @Property
  boolean roundtripPatternBuilder(@ForAll("simpleQueries") String query) {
    Expr ast1 = AstBuilders.fromQuery(query, new AstBuilderPattern()::translate);
    String printed = AstPrinter.toString(ast1);
    Expr ast2 = AstBuilders.fromQuery(printed, new AstBuilderPattern()::translate);
    return ast1.equals(ast2);
  }

  @Property
  boolean roundtripVisitorBuilder(@ForAll("simpleQueries") String query) {
    Expr ast1 = AstBuilders.fromQuery(query, new AstBuilderVisitor()::translate);
    String printed = AstPrinter.toString(ast1);
    Expr ast2 = AstBuilders.fromQuery(printed, new AstBuilderVisitor()::translate);
    return ast1.equals(ast2);
  }

  @Property
  boolean bothBuildersAreIdentical(@ForAll("simpleQueries") String query) {
    Expr astPattern = AstBuilders.fromQuery(query, new AstBuilderPattern()::translate);
    Expr astVisitor = AstBuilders.fromQuery(query, new AstBuilderVisitor()::translate);
    return astPattern.equals(astVisitor);
  }

  // ---------- @Provide-Methods for Arbitraries ----------

  @Provide
  Arbitrary<String> fields() {
    return Arbitraries.of("title", "artist", "genre", "year");
  }

  @Provide
  Arbitrary<String> stringLiterals() {
    return Arbitraries.strings()
        .withChars("abcxyz")
        .ofMinLength(1)
        .ofMaxLength(5)
        .map(s -> "\"" + s + "\"");
  }

  @Provide
  Arbitrary<String> numberLiterals() {
    return Arbitraries.integers().between(1900, 2025).map(Object::toString);
  }

  @Provide
  Arbitrary<String> comparisons() {
    Arbitrary<String> ops = Arbitraries.of("==", "!=", "<", "<=", ">", ">=");

    Arbitrary<String> stringComp =
        Combinators.combine(fields(), ops, stringLiterals())
            .as((f, op, lit) -> f + " " + op + " " + lit);

    Arbitrary<String> numberComp =
        Combinators.combine(Arbitraries.of("year"), ops, numberLiterals())
            .as((f, op, lit) -> f + " " + op + " " + lit);

    return Arbitraries.oneOf(stringComp, numberComp);
  }

  @Provide
  Arbitrary<String> simpleQueries() {
    return comparisons()
        .list()
        .ofMinSize(1)
        .ofMaxSize(3)
        .map(
            list -> {
              if (list.size() == 1) return list.getFirst();
              StringBuilder sb = new StringBuilder();
              for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                  String conn = Arbitraries.of(" and ", " or ").sample();
                  sb.append(conn);
                }
                sb.append(list.get(i));
              }
              return sb.toString();
            });
  }
}
