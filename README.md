# Parser
A compiler program for a made-up language "x"
This project required me to complete the Compiler program for a made-up language ‘x’, building on the modification to Lexer that we made in the previous assignment to recognize new tokens. It required me to modify the Parser introducing new production rules based on the new added token and a few other rules, including the production rule for a switch statement.
As a second step, it required me to create two new Visitor classes. An OffsetVisitor to calculate the positioning of each node so that the final result would be a well-balanced (not in a technical sense) tree. A second Visitor would draw the tree using the positions calculated by the OffsetVisitor.
