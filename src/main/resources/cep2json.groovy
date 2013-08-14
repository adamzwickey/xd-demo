
split = payload.tokenize(",")
return "{\"storeId\":" + split.get(0) + ", \"amount\":" + split.get(1) + "}"
