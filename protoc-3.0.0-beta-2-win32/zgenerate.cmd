@echo off
protoc.exe --java_out=../src/ ./XMessage.proto
protoc.exe --csharp_out=../../Rosetta/Rosetta/NetSystem/ ./XMessage.proto
pause