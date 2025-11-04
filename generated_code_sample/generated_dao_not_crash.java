@Override
public Object getRandomBook(final Continuation<? super Book> $completion) {
  final String _sql = "SELECT * FROM BookInfo ORDER BY RANDOM() LIMIT 1";
  final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
  final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
  return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<Book>() {
    @Override
    @Nullable
    public Book call() throws Exception {
      __db.beginTransaction();
      try {
        final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final LongSparseArray<ArrayList<Page>> _collectionPages = new LongSparseArray<ArrayList<Page>>();
          while (_cursor.moveToNext()) {
            final long _tmpKey;
            _tmpKey = _cursor.getLong(_cursorIndexOfId);
            if (!_collectionPages.containsKey(_tmpKey)) {
              _collectionPages.put(_tmpKey, new ArrayList<Page>());
            }
          }
          _cursor.moveToPosition(-1);
          __fetchRelationshipPageAscomExampleOrderbyrandomsampleDbPage(_collectionPages);
          final Book _result;
          if (_cursor.moveToFirst()) {
            final BookInfo _tmpBookInfo;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            _tmpBookInfo = new BookInfo(_tmpId,_tmpTitle);
            final ArrayList<Page> _tmpPagesCollection;
            final long _tmpKey_1;
            _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
            _tmpPagesCollection = _collectionPages.get(_tmpKey_1);
            _result = new Book(_tmpBookInfo,_tmpPagesCollection);
          } else {
            _result = null;
          }
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      } finally {
        __db.endTransaction();
      }
    }
  }, $completion);
}
