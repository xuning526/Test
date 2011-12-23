from twisted.application import service, internet
from nevow import appserver, athena, loaders, tags as T
from chat import ChatRoom, Buddy

room = ChatRoom()

class ChatPage(athena.LivePage):
  docFactory = loaders.stan(T.html[
      T.head(render=T.directive('liveglue')), # name is critical!
      T.body(render=T.directive('buddy')), # name is critical!
    ])

  def render_buddy(self, ctx, data):
    buddy = room.addBuddy() 
    buddy.setFragmentParent(self)
    return ctx.tag[buddy]

  def child_(self, ctx):
    return ChatPage()

application = service.Application("chat")
port = 8080
res = ChatPage()
site = appserver.NevowSite(res)
webService = internet.TCPServer(port, site)
webService.setServiceParent(application)

