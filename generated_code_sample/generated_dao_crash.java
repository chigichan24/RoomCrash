@Override
public Object getRandomBook(final Continuation<? super Book> $completion) {
  final String _sql = "SELECT * FROM BookInfo ORDER BY RANDOM() LIMIT 1";
  return DBUtil.performSuspending(__db, true, true, (_connection) -> {
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    try {
      final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
      final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
      final LongSparseArray<ArrayList<Page>> _collectionPages = new LongSparseArray<ArrayList<Page>>();
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_columnIndexOfId);
        if (!_collectionPages.containsKey(_tmpKey)) {
          _collectionPages.put(_tmpKey, new ArrayList<Page>());
        }
      }
      _stmt.reset();
      __fetchRelationshipPageAscomExampleOrderbyrandomsampleDbPage(_connection, _collectionPages);
      final Book _result;
      if (_stmt.step()) {
        final BookInfo _tmpBookInfo;
        final long _tmpId;
        _tmpId = _stmt.getLong(_columnIndexOfId);
        final String _tmpTitle;
        _tmpTitle = _stmt.getText(_columnIndexOfTitle);
        _tmpBookInfo = new BookInfo(_tmpId,_tmpTitle);
        final ArrayList<Page> _tmpPagesCollection;
        final long _tmpKey_1;
        _tmpKey_1 = _stmt.getLong(_columnIndexOfId);
        _tmpPagesCollection = _collectionPages.get(_tmpKey_1);
        _result = new Book(_tmpBookInfo,_tmpPagesCollection);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _stmt.close();
    }
  }, $completion);
}
