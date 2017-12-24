package v1.places

import play.api.mvc.{MessagesRequestHeader, PreferredMessagesProvider}

trait PlaceRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider
