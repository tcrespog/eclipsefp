{-# LANGUAGE CPP, DeriveDataTypeable #-}
module Language.Haskell.Exts.Comments where

import Language.Haskell.Exts.Syntax

#ifdef __GLASGOW_HASKELL__
#ifdef BASE4
import Data.Data
#else
import Data.Generics (Data(..),Typeable(..))
#endif
#endif

data Comment = SingleLine SrcLoc String
             | MultiLine  SrcLoc String
#ifdef __GLASGOW_HASKELL__
  deriving (Eq,Show,Typeable,Data)
#else
  deriving (Eq,Show)
#endif