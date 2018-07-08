# Tests implemented 


## **Box** class tests

- [x] boxPositions
- [x] boxValue				  
- [x] boxColor				  
- [x] updateTest				
- [x] boxFilledByADie			
- [x] boxNotOpened				
- [x] boxOpened
- [x] boxOpenedButColorConstraintsBlock	
- [x] boxOpenedButColorConstraintsDontBlock	
- [x] boxOpenedButValueCOnstraintsBlock	
- [x] boxOpenedButValueConstraintsDontBlock	
- [x] equalsColor				
- [x] differentColor	
- [x] checkGetConstraint
- [x] checkRemoveDie
- [x] checkToString
- [x] boxCopy		

## **DicePool** class tests

- [x] checkPoolSize				
- [x] checkDieToRemove		
- [x] checkDicePool
- [x] checkGetDieFromPool
- [x] checkRemoveDieFromPool
- [x] checkInsertDieInPool	

## **Die** class tests

- [x] dieValue				
- [x] dieColor				
- [x] bothValidParameters			
- [x] colorNotCapsSensitive		
- [x] dieCopy	

## **Grid** class tests

- [x] nullStringPassed			
- [x] invalidDifficultyPassed		
- [x] validParameterPassed			
- [x] nullConstraintPassed			
- [x] createIndexesOutOfBound               
- [x] boxAlreadyCreated                    
- [x] invalidStringPassed                   
- [x] invalidValueConstraintPassed          
- [x] validValueConstraintPassed            
- [x] validColorConstraintPassed            
- [x] validNoneConstraintPassed             
- [x] checkBoxOpened                        
- [x] nullDiePassed                         
- [x] insertIndexesOutOfBound      
- [x] insertDieAfterFirst         
- [x] gridNotInitialized                    
- [x] tryToInsertDieReturnFalse             
- [x] tryToInsertDieReturnTrue 
- [x] gridIsNotiinitialized
- [x] gridIsOk
- [x] gridIsNotComplete
- [x] checkToString
- [x] checkString
- [x] checkRemoveDieFromXY
- [x] checkInitializeAllObservers
- [x] gridCopy
- [x] gridCopyModification         

## **DieToConstraintsAdapter** class tests
- [x] right_value
- [x] checkDie

## **Player** class tests
- [x] checkCreator
- [x] checkSetGrid
- [x] checkSetObj
- [x] checkHasSelectedAGrid

## **PrivateObjective** class tests

- [x] checkColor			
- [x] checkType
- [x] checkShowPrivate
- [x] checkCalculate

## **PublicObjective** class tests
- [x] publicObjective1Test
- [x] publicObjective2Test
- [x] publicObjective3Test
- [x] publicObjective4Test
- [x] publicObjective5Test
- [x] publicObjective6Test
- [x] publicObjective7Test
- [x] publicObjective7WrongTest
- [x] publicObjective8Test
- [x] publicObjective9Test
- [x] publicObjective9Test_ForTwoDiagonals
- [x] publicObjective9Test_ForNotConsequentDiagonals
- [x] publicObjective10Test
- [x] publicObjective10Test_Alternative
- [x] toStringTest
	
## **MatchModel** class tests
- [x] checkCreator
- [x] checkAskTurn
- [x] checkSetPGridandCheckEnd
- [x] checkPrepareForNextRound
- [x] checkRoundTrack
- [x] checkMatchDicepool
- [x] checkValidUpdateTurn
- [x] checkSetPlayerToDisconnect
- [x] checkHasPlayerChosenAGrid
- [x] checkInsertDieOpGetGridsForPlayersGetPlCurrentGrid
- [x] checkPrivateObjective
- [x] checkMatchModelCreator
- [x] checkDisconnection

## **ConfigurationHandler** class tests
- [x] getInstanceTest
- [x] getGridsTest
- [x] getPublicObjectiveTest
- [x] getToolCardsTest
- [x] getGsonForToolCardsTest
