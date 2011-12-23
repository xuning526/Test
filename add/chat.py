from nevow.athena import LiveElement, expose
from nevow.loaders import xmlfile

class ChatRoom(object):
  def __init__(self):
    self._buddyL = []

  def tellAll(self, msg):
    for buddy in self._buddyL:
      buddy.hear(msg)

  def addBuddy(self):
    buddy = Buddy(self)
    self._buddyL.append(buddy)
    return buddy

  def removeBuddy(self, buddy):
    try:
      self._buddyL.remove(buddy)
    except ValueError:
      pass
# add a new line for test
#chat = ChatRoom().addBuddy # why?

class Buddy(LiveElement):
  docFactory = xmlfile('template.xhtml')
  jsClass = u'Chat.Buddy'

  def __init__(self, room):
    self.room = room

  @expose
  def setUsername(self, name):
    self.name = name
    self.room.tellAll('%s has joined the room!' % name)

  @expose
  def say(self, msg):
    msg = "%s says: %s" % (self.name, msg,)
    self.room.tellAll(msg)

  def hear(self, msg):
    self.callRemote('displayMessage', msg)

  @expose
  def leave(self):
    self.room.removeBuddy(self)
