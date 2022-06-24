grammar RustMSVCName;

parse
    : qualifiedName EOF
    ;

qualifiedName
    : ( globalNamespaceToken=NAMESPACE_SEP )? ( namespaceSegments+=qualifiedNameSegment NAMESPACE_SEP )* namespaceSegments+=qualifiedNameSegment
    ;

qualifiedNameSegment
    : name=WORD templateArguments?
    ;

templateArguments
    : '<'  arguments+=qualifiedName ( ',' arguments+=qualifiedName )* '>'
    ;

NAMESPACE_SEP : '::' ;
WORD : (LETTER | DIGIT)+ ;
fragment LETTER : 'a'..'z' | 'A'..'Z' | '_' ;
fragment DIGIT : '0'..'9' ;

WS : [ \t]+ -> skip ;
