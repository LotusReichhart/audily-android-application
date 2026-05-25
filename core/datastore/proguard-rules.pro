# Protocol Buffers (Protobuf) Lite rules to prevent R8 from obfuscating/shrinking generated message fields used in reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
