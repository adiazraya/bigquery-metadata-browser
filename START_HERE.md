# 🚀 START HERE

## Welcome to IncidenciaBQ!

Your BigQuery browser is ready. Follow these 3 simple steps:

---

## ⚡ 3 Steps to Get Running

### 1️⃣ Add Your Credentials

Get the service account JSON key for:
- **Email**: `datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com`
- **Project**: `ehc-alberto-diazraya-35c897`

Save it as: **`service-account-key.json`** (in this folder)

### 2️⃣ Run Setup

```bash
./setup.sh
```

### 3️⃣ Start Application

```bash
./run.sh
```

Then open: **http://localhost:8080**

---

## 📖 Need More Help?

| What you need | Read this file |
|---------------|----------------|
| **Getting started guide** | [GETTING_STARTED.md](GETTING_STARTED.md) |
| **Quick reference** | [QUICKSTART.md](QUICKSTART.md) |
| **Full documentation** | [README.md](README.md) |
| **Deploy to Heroku** | [DEPLOYMENT.md](DEPLOYMENT.md) |
| **Testing guide** | [TEST.md](TEST.md) |
| **Architecture** | [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) |
| **Feature summary** | [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) |
| **Project structure** | [PROJECT_STRUCTURE.txt](PROJECT_STRUCTURE.txt) |

---

## ✨ What You'll Get

- 📊 Browse all BigQuery datasets
- 📋 View tables in each dataset
- 🎨 Beautiful, modern UI
- ⚡ Fast and responsive
- 🔒 Secure authentication
- ⏱️ Performance timing logs

---

## 🆘 Troubleshooting

**Problem**: Can't find service account key?
- Download from Google Cloud Console → IAM & Admin → Service Accounts

**Problem**: Port 8080 in use?
- Edit `src/main/resources/application.properties` and change `server.port=8081`

**Problem**: Connection failed?
- Check service account has `BigQuery Data Viewer` role
- Verify project ID is correct

**More help?** See [TEST.md](TEST.md) troubleshooting section

---

## 🎯 That's It!

You're ready to browse your BigQuery data!

**Questions?** Check the documentation files above.

**Happy browsing!** 🎉

