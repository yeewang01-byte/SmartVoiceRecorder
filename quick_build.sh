#!/bin/bash

# 智能 APK 快速构建脚本
# 自动配置环境并生成 APK 到桌面

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DESKTOP_PATH="$HOME/Desktop"
OUTPUT_NAME="SmartVoiceRecorder-1.0.0.apk"
OUTPUT_PATH="$DESKTOP_PATH/$OUTPUT_NAME"

echo "🚀 智能 APK 快速构建脚本"
echo "======================================"

# 第一步：检查和安装必要的工具
echo ""
echo "📋 第一步：检查开发环境..."

# 检查 Homebrew
if ! command -v brew &> /dev/null; then
    echo "⚠️  Homebrew 未安装，正在安装..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)" || {
        echo "❌ Homebrew 安装失败"
        exit 1
    }
    echo "✅ Homebrew 安装成功"
fi

# 检查 Java
if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -q "version"; then
    echo "⚠️  Java 17 未安装或配置错误，正在安装..."
    brew install openjdk@17 || {
        echo "❌ Java 安装失败"
        exit 1
    }
    
    # 配置 JAVA_HOME
    if [ -d "/opt/homebrew/opt/openjdk@17" ]; then
        export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
    elif [ -d "/usr/local/opt/openjdk@17" ]; then
        export JAVA_HOME="/usr/local/opt/openjdk@17"
    fi
    
    echo "✅ Java 17 安装成功"
fi

# 检查 Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Android/sdk"
    else
        echo "⚠️  Android SDK 未安装，正在配置..."
        
        # 创建 SDK 目录
        mkdir -p "$HOME/Library/Android/sdk"
        export ANDROID_HOME="$HOME/Library/Android/sdk"
        
        # 下载并配置 SDK
        echo "📥 下载 Android SDK Command-line Tools..."
        curl -o /tmp/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-mac-10191603_latest.zip 2>/dev/null || {
            echo "⚠️  SDK 下载失败（可能是网络问题），使用备用方案..."
        }
        
        if [ -f /tmp/cmdline-tools.zip ]; then
            unzip -q /tmp/cmdline-tools.zip -d "$ANDROID_HOME"
            mkdir -p "$ANDROID_HOME/cmdline-tools/latest"
            mv "$ANDROID_HOME/cmdline-tools"/* "$ANDROID_HOME/cmdline-tools/latest" 2>/dev/null || true
        fi
    fi
    
    echo "✅ Android SDK 路径: $ANDROID_HOME"
fi

# 添加到 PATH
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/34.0.0/bin:$PATH"

echo ""
echo "🔨 第二步：构建 APK..."
echo "======================================"

cd "$SCRIPT_DIR"

# 清理旧的构建
echo "🧹 清理旧的构建文件..."
./gradlew clean > /dev/null 2>&1 || true

# 构建 Release APK
echo "⏳ 正在构建 APK（这会花费 2-5 分钟）..."
if ./gradlew assembleRelease -q; then
    echo "✅ APK 构建成功"
else
    echo "❌ APK 构建失败"
    echo ""
    echo "💡 故障排查建议："
    echo "  1. 检查网络连接"
    echo "  2. 运行: ./gradlew --version 检查 Gradle"
    echo "  3. 查看 BUILD_GUIDE.md 获取详细帮助"
    exit 1
fi

# 第三步：复制到桌面
echo ""
echo "📦 第三步：输出 APK 到桌面..."
echo "======================================"

APK_SOURCE="$SCRIPT_DIR/app/build/outputs/apk/release/app-release.apk"

if [ -f "$APK_SOURCE" ]; then
    cp "$APK_SOURCE" "$OUTPUT_PATH"
    
    echo "✅ APK 已成功生成！"
    echo ""
    echo "📱 APK 信息："
    echo "  位置: $OUTPUT_PATH"
    echo "  大小: $(du -h "$OUTPUT_PATH" | cut -f1)"
    echo "  应用名: 智能录音笔 (Smart Voice Recorder)"
    echo "  版本: 1.0.0"
    echo ""
    echo "🎉 下一步："
    echo "  1. 在桌面找到: $OUTPUT_NAME"
    echo "  2. 通过 adb 安装: adb install ~/Desktop/$OUTPUT_NAME"
    echo "  3. 或在 Android Studio 中拖入模拟器"
    echo "  4. 在手机上开始使用！"
else
    echo "❌ 错误：APK 文件未生成"
    echo "位置应该在: $APK_SOURCE"
    exit 1
fi

echo ""
echo "======================================"
echo "✨ 构建完成！"
