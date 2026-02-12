Ref makeListOfList (int x){
    return makeListFromList (makeReverseList(x));
}

Ref makeIncreasingList(int x) {
  return makeIncreasingListHelper(1, x);
}

Ref makeIncreasingListHelper(int curr, int end) {
  if (curr > end) return nil;
  return makeList(curr, 1) . makeIncreasingListHelper(curr + 1, end);
}

Ref makeListFromList (Ref list){
    if (isNil(list) == 1) return list;
    return list . makeListFromList ((Ref) right (list));
}

Ref makeReverseList (int x){
    if (x == 0)  return nil;    
    return x . makeReverseList(x -1);
}

Ref makeList(int len, int current){
    if (len == 0)  return nil;
    return current . makeList(len -1, current + 1);
}

Ref append(Ref leftList, Ref rightList){
  if (isNil(leftList) == 1) {
    return rightList;
  }
  return left(leftList) . append((Ref) right(leftList), rightList);
}

Ref reverse(Ref list) {
 if (isNil(list) == 1) {
    return nil;
  }
  return append(reverse((Ref) right(list)), left(list) . nil);
}

int areSorted(Ref list) {
  if (isNil(list) == 1) {
    return 1;
  }
  if (isListSorted((Ref) left(list)) == 0) {
    return 0;
  }
  return areSorted((Ref) right(list));
}

int isListSorted(Ref list) {
  if (isNil(list) == 1) {
    return 1;  
  }
  Ref rest = (Ref) right(list);
  if (isNil(rest) == 1) {
    return 1;  
  }
  if ((int) left(list) > (int) left(rest)) {
    return 0;
  }
  return isListSorted(rest);
}

/**
Question 5a: No. Immutable Quandary would not be Turing-complete without if statements. Recusion would not be practical, because you cannot check for the base case. So, many fuctions (such as factorial and the above problems) would not be possible. 

Question 5b: No. In mutable languages, recursive functions can be rewritten iteratively via loops. However, in this case, loops do not exist, because the language is immutable. So, unbounded functions (such as factorial and the above problems) rely on recursion. Without recursion, these functions would not be possible, and Immutable Quandary would not be Turing-complete. 
 **/
