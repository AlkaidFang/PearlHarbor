@echo off
protoc.exe --java_out=../src/ ./XMessage.proto
protoc.exe --csharp_out=../../Rosetta/Unity/Assets/Core/NetSystem/ ./XMessage.proto
pause