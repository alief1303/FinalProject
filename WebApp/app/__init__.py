from datetime import timedelta
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_admin import Admin
from flask_login import LoginManager
from flask_migrate import Migrate
from flask_bootstrap import Bootstrap

app = Flask(__name__)
app.send_file_max_age_default = timedelta(seconds=1)
app.config.from_object('config')
app.config["SECRET_KEY"]="123456789"
db = SQLAlchemy(app)
admin = Admin(app, template_mode='bootstrap3')
login_manager = LoginManager()
login_manager.init_app(app)
# Handles all migrations.
migrate = Migrate(app, db)
bootstrap = Bootstrap(app)

from app import views, models
