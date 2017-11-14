package com.fuh.turingmachine

import java.util.HashSet


class TuringMachine {
    private val stateSpace: MutableSet<String> = HashSet()
    private val transitionSpace: MutableSet<Transition> = HashSet()
    private var startState: String? = ""
    private var acceptState: String? = ""
    private var rejectState: String? = ""

    private var tape: String? = ""
    private var currentState: String? = ""
    private var currentSymbol: Int = 0

    fun run(input: String, isSilentMode: Boolean): Boolean {
        currentState = startState
        tape = input

        while (currentState != acceptState && currentState != rejectState) {
            var isTransitionFound = false
            var currentTransaction: Transition? = null

            if (!isSilentMode) {
                if (currentSymbol > 0) {
                    println(tape!!.substring(0, currentSymbol) + " " + currentState + " " + tape!!.substring(currentSymbol))
                } else {
                    println(" " + currentState + " " + tape!!.substring(currentSymbol))
                }
            }


            val transitionsIterator = transitionSpace.iterator()
            while (transitionsIterator.hasNext() && !isTransitionFound) {
                val nextTransition = transitionsIterator.next()
                if (nextTransition.readState == currentState && nextTransition.readSymbol == tape!![currentSymbol]) {
                    isTransitionFound = true
                    currentTransaction = nextTransition
                }
            }

            if (!isTransitionFound) {
                System.err.println("There is no valid transition for this phase! (state=" + currentState + ", symbol=" + tape!![currentSymbol] + ")")

                return false
            } else {
                currentState = currentTransaction!!.writeState
                val tempTape = tape!!.toCharArray()
                tempTape[currentSymbol] = currentTransaction.writeSymbol
                tape = String(tempTape)
                if (currentTransaction.moveDirection == true) {
                    currentSymbol++
                } else {
                    currentSymbol--
                }

                if (currentSymbol < 0)
                    currentSymbol = 0

                while (tape!!.length <= currentSymbol) {
                    tape += "_"
                }
            }
        }

        return currentState == acceptState
    }

    fun addState(newState: String): Boolean {
        return if (stateSpace.contains(newState)) {
            false
        } else {
            stateSpace.add(newState)

            true
        }
    }

    fun setStartState(newStartState: String): Boolean {
        return if (stateSpace.contains(newStartState)) {
            startState = newStartState

            true
        } else {
            false
        }
    }

    fun setAcceptState(newAcceptState: String): Boolean {
        return if (stateSpace.contains(newAcceptState) && rejectState != newAcceptState) {
            acceptState = newAcceptState

            true
        } else {
            false
        }

    }

    fun setRejectState(newRejectState: String): Boolean {
        return if (stateSpace.contains(newRejectState) && acceptState != newRejectState) {
            rejectState = newRejectState

            true
        } else {
            false
        }
    }

    fun addTransition(rState: String, rSymbol: Char, wState: String, wSymbol: Char, mDirection: Boolean): Boolean {
        if (!stateSpace.contains(rState) || !stateSpace.contains(wState)) {
            return false
        }

        var conflict = false
        val transitionsIterator = transitionSpace.iterator()
        while (transitionsIterator.hasNext() && !conflict) {
            val nextTransition = transitionsIterator.next()
            if (nextTransition.isConflicting(rState, rSymbol)) {
                conflict = true
            }

        }
        return if (conflict) {
            false
        } else {
            val newTransition = Transition()
            newTransition.readState = rState
            newTransition.readSymbol = rSymbol
            newTransition.writeState = wState
            newTransition.writeSymbol = wSymbol
            newTransition.moveDirection = mDirection
            transitionSpace.add(newTransition)

            true
        }
    }

    class Transition {
        var readState: String? = null
        var readSymbol: Char = ' '
        var writeState: String? = null
        var writeSymbol: Char = ' '
        var moveDirection: Boolean = false    //true is right, false is left

        fun isConflicting(state: String, symbol: Char): Boolean {
            return state == readState && symbol == readSymbol
        }
    }
}