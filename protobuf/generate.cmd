@echo off
protoc.exe --java_out=../src/ ./Packet.proto
pause