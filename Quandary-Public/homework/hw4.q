mutable Q main(int arg) {
  if (arg == 1) {
    Ref obj = problem1(17);
  }
  if (arg == 2) {
    Ref obj = problem2(100);
  }
  if (arg == 3) {
    Ref obj = problem3(17);
  }
  if (arg == 4) {
    Ref obj = problem4(100);
  }

  return 0;
}

Ref problem1(int x) {
  if (x == 0) return nil;
  return x . problem1(x - 1);
}

mutable Ref makeCycle() {
    mutable Ref a = nil. nil;
    mutable Ref b = nil . nil;
    setLeft(a, b);
    setLeft(b, a);
    return nil;
}

mutable Ref problem2(int x) {
    if (x == 0) return nil;
    Ref dummy = makeCycle();  
    return problem2(x - 1);  
}

Ref problem3(int x) {
  if (x  == 0) return nil; 

  Ref a = nil . nil; 
  free(a);

  return problem3(x - 1);
}

Ref problem4Helper() {
  Ref a = nil.nil;
  return nil; 
}

Ref problem4(int x) {
  if (x == 0) return nil; 
  Ref dummy = problem4Helper();
  return problem4(x - 1);
}

