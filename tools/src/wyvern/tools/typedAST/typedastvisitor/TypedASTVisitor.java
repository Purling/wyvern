package wyvern.tools.typedAST.typedastvisitor;

import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.core.Script;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ForwardDeclaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.Instantiation;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assertion;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Case;
import wyvern.tools.typedAST.core.expressions.Try;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.Match;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.CharacterConstant;
import wyvern.tools.typedAST.core.values.FloatConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.core.values.RationalConstant;

public abstract class TypedASTVisitor<S, T> {
    public abstract T visit(S state, NameBindingImpl ast);
    public abstract T visit(S state, DeclSequence ast);
    public abstract T visit(S state, DefDeclaration ast);
    public abstract T visit(S state, ForwardDeclaration ast);
    public abstract T visit(S state, EffectDeclaration ast);
    public abstract T visit(S state, ImportDeclaration ast);
    public abstract T visit(S state, Instantiation ast);
    public abstract T visit(S state, ModuleDeclaration ast);
    public abstract T visit(S state, TypeAbbrevDeclaration ast);
    public abstract T visit(S state, TypeDeclaration ast);
    public abstract T visit(S state, TypeVarDecl ast);
    public abstract T visit(S state, ValDeclaration ast);
    public abstract T visit(S state, VarDeclaration ast);
    public abstract T visit(S state, Application ast);
    public abstract T visit(S state, Assertion ast);
    public abstract T visit(S state, Assignment ast);
    public abstract T visit(S state, Case ast);
    public abstract T visit(S state, Fn ast);
    public abstract T visit(S state, Invocation ast);
    public abstract T visit(S state, LetExpr ast);
    public abstract T visit(S state, Match ast);
    public abstract T visit(S state, New ast);
    public abstract T visit(S state, TaggedInfo ast);
    public abstract T visit(S state, Variable ast);
    public abstract T visit(S state, RationalConstant ast);
    public abstract T visit(S state, BooleanConstant ast);
    public abstract T visit(S state, CharacterConstant ast);
    public abstract T visit(S state, FloatConstant ast);
    public abstract T visit(S state, IntegerConstant ast);
    public abstract T visit(S state, StringConstant ast);
    public abstract T visit(S state, UnitVal ast);
    public abstract T visit(S state, Script ast);
    public abstract T visit(S state, Sequence ast);
    public abstract T visit(S state, DSLLit ast);
    public abstract T visit(S state, Try ast);
}
