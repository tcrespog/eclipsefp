

newtype CodeGenModule a = CGM (StateT CGMState IO a)
   deriving (Monad, MonadState [s], MonadIO)
