from flask_login._compat import unicode
from app import db
from flask_login import UserMixin, login_user, AnonymousUserMixin
from . import login_manager
from flask import request

Relationship = db.Table('relationship', db.Model.metadata,
                        db.Column('movie_id', db.Integer, db.ForeignKey('movie.movie_id')),
                        db.Column('company_name', db.String(32), db.ForeignKey('cinema.company_name'))
                        )

@login_manager.user_loader
def get_user(ident):
  return User.query.get(int(ident))


class User(UserMixin, db.Model):
    __tablename__ = 'user'
    user_id = db.Column(db.Integer, primary_key=True)
    user_name = db.Column(db.String(32), index=True)
    password = db.Column(db.String(32), index=True)
    mail = db.Column(db.String(32), index=True)
    gender = db.Column(db.String(8), index=True)
    avatar = db.Column(db.String(128), index=True)
    age = db.Column(db.Integer)
    phone_number = db.Column(db.Integer)
    money = db.Column(db.Integer)
    accumulated_amount = db.Column(db.Integer)
    user_vip_level = db.Column(db.Integer)
    orders = db.relationship('Order', backref='users', lazy='dynamic')
    records = db.relationship('Record', backref='users', lazy='dynamic')
    comments = db.relationship('Comment', backref='users', lazy='dynamic')

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return unicode(self.user_id)

    def __repr__(self):
        return '<User %r>' % self.user_id


class Movie(db.Model):
    __tablename__ = 'movie'
    movie_id = db.Column(db.Integer, primary_key=True)
    movie_name = db.Column(db.String(32), index=True)
    movie_type = db.Column(db.String(64), index=True)
    movie_length = db.Column(db.String(32), index=True)
    introduction = db.Column(db.String(512), index=True)
    country = db.Column(db.String(32), index=True)
    special_effect = db.Column(db.String(8), index=True)
    comment = db.Column(db.String(128), index=True)
    actors = db.Column(db.String(128), index=True)
    director = db.Column(db.String(32), index=True)
    poster = db.Column(db.String(128), index=True)
    serial_number = db.Column(db.String(40), server_default='0000000000000000000000000000000000000000')
    premiere_date = db.Column(db.Date)
    date = db.Column(db.Date)
    start_time = db.Column(db.Time)
    finish_time = db.Column(db.Time)
    scene = db.Column(db.Integer, default=0)
    projection_hall = db.Column(db.Integer)
    price = db.Column(db.Integer)
    score = db.Column(db.Integer)
    orders = db.relationship('Order', backref='movies', lazy='dynamic')
    comments = db.relationship('Comment', backref='movies', lazy='dynamic')
    cinemas = db.relationship('Cinema', secondary=Relationship)


class Order(db.Model):
    __tablename__ = 'order'
    order_id = db.Column(db.Integer, primary_key=True)
    order_date = db.Column(db.DateTime, index=True)
    seat_number = db.Column(db.Integer)
    ticket_key = db.Column(db.String(256), index=True)
    order_user = db.Column(db.Integer, db.ForeignKey('user.user_id'))
    order_movie = db.Column(db.Integer, db.ForeignKey('movie.movie_id'))
    comments = db.relationship('Comment', backref='orders', lazy='dynamic')


class Cinema(db.Model):
    __tablename__ = 'cinema'
    company_name = db.Column(db.String(32), primary_key=True)
    location = db.Column(db.String(32), index=True)
    phone_number = db.Column(db.String(16), index=True)
    service = db.Column(db.String(128), index=True)
    movies = db.relationship('Movie', secondary=Relationship)


class Record(db.Model):
    __tablename__ = 'record'
    record_id = db.Column(db.Integer, primary_key=True)
    amount = db.Column(db.Integer)
    trade_date = db.Column(db.DateTime, index=True)
    trade_user = db.Column(db.Integer, db.ForeignKey('user.user_id'))


class Comment(db.Model):
    __tablename__ = 'comment'
    comment_id = db.Column(db.Integer, primary_key=True)
    body = db.Column(db.Text)
    mark = db.Column(db.Integer)
    author_id = db.Column(db.Integer, db.ForeignKey('user.user_id'))
    movie_id = db.Column(db.Integer, db.ForeignKey('movie.movie_id'))
    order_id = db.Column(db.Integer, db.ForeignKey('order.order_id'))


class Version(db.Model):
    __tablename__ = 'version'
    version_number = db.Column(db.String(32), primary_key=True)
