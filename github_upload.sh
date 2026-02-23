#!/bin/bash

# GitHub Actions 自动构建脚本
# 一键上传项目到 GitHub 并自动触发 APK 构建

echo "🚀 Smart Voice Recorder - GitHub 自动构建"
echo "=========================================="
echo ""

# 检查 Git 是否安装
if ! command -v git &> /dev/null; then
    echo "❌ git 未安装"
    echo "请先安装 git: https://git-scm.com/download/mac"
    exit 1
fi

echo "✅ git 已安装"
echo ""

# 检查是否已初始化 Git
if [ ! -d ".git" ]; then
    echo "📝 初始化 Git 仓库..."
    git init
    git add .
    git commit -m "Initial commit: Smart Voice Recorder"
else
    echo "✅ Git 仓库已初始化"
fi

echo ""
echo "=========================================="
echo "📋 GitHub 账户设置"
echo "=========================================="
echo ""
echo "请按照以下步骤操作："
echo ""
echo "1️⃣  访问 https://github.com/new 创建新仓库"
echo "    - Repository name: SmartVoiceRecorder"
echo "    - Description: Smart Voice Recorder App"
echo "    - Public（开放） 或 Private（私密）"
echo "    - ❌ 不要选 'Add README'"
echo ""
echo "2️⃣  创建后，复制仓库的 HTTPS URL"
echo "    示例: https://github.com/your-username/SmartVoiceRecorder.git"
echo ""
echo "3️⃣  在下面粘贴这个 URL"
echo ""

read -p "请输入你的 GitHub 仓库 URL: " REPO_URL

if [ -z "$REPO_URL" ]; then
    echo "❌ 未输入 URL，退出"
    exit 1
fi

# 验证 URL 格式
if [[ ! "$REPO_URL" =~ ^https://github\.com/.*\.git$ ]]; then
    echo "⚠️  URL 格式可能不正确"
    echo "预期格式: https://github.com/username/repo.git"
    read -p "继续吗？(y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "🔌 正在连接到 GitHub..."

# 添加远程仓库
if git remote | grep -q origin; then
    echo "更新现有的远程仓库..."
    git remote set-url origin "$REPO_URL"
else
    echo "添加远程仓库..."
    git remote add origin "$REPO_URL"
fi

echo ""
echo "📤 正在推送代码到 GitHub..."
echo "   （可能需要输入你的 GitHub 用户名和密码或 token）"
echo ""

# 推送到主分支
git branch -M main
if git push -u origin main 2>&1; then
    echo ""
    echo "✅ 代码已成功推送到 GitHub！"
    echo ""
else
    echo ""
    echo "⚠️  推送失败，可能的原因："
    echo "   1. 网络连接问题"
    echo "   2. GitHub 用户名或 token 错误"
    echo "   3. 仓库权限问题"
    echo ""
    exit 1
fi

echo ""
echo "=========================================="
echo "🔨 GitHub Actions 自动构建已启动！"
echo "=========================================="
echo ""
echo "现在自动化构建过程已启动，请按下列步骤查看结果："
echo ""
echo "1️⃣  访问: https://github.com/your-username/SmartVoiceRecorder/actions"
echo ""
echo "2️⃣  等待构建完成（通常需要 5-10 分钟）"
echo ""
echo "3️⃣  构建完成后，点击最新的构建记录"
echo ""
echo "4️⃣  在页面底部找到 'Artifacts' 部分"
echo ""
echo "5️⃣  下载 'SmartVoiceRecorder-APK' 压缩包"
echo ""
echo "6️⃣  解压后即可得到 'app-release.apk' 文件"
echo ""
echo "7️⃣  使用以下命令安装到设备："
echo "    adb install app-release.apk"
echo ""
echo "=========================================="
echo "💡 提示"
echo "=========================================="
echo ""
echo "• GitHub Actions 完全免费"
echo "• 构建在 GitHub 的官方服务器上运行"
echo "• 无需本地安装任何开发工具"
echo "• 每次 push 时自动构建（可设置分支）"
echo "• 可以设置发布版本（Tags）自动生成 Release"
echo ""
echo "更多信息请参考: FAST_APK_GUIDE.md"
echo ""
echo "✨ 完成！"
