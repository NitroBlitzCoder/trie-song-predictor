# Trie Song Predictor

Built a Java app that uses a custom Trie data structure to predict words, detect language, and match songs in real time as you type. Has a fully custom dark-theme GUI built with Java Swing.

## What it does

- Predicts the next character and next word as you type based on word frequency data stored in the Trie
- Detects whether a typed word is English, Spanish, or French
- Matches typed words against lyrics from different songs and figures out which song you're referencing
- Plays the matched song's audio with a draggable scrubber bar
- Words are color coded in the input — green if it's a known word, red if not, yellow for whatever you're currently typing

## Built with

- Java
- Java Swing for the UI (custom paintComponent, no UI libraries)
- javax.sound.sampled for audio
- HashMap, TreeMap, HashSet for the underlying data structures

## Setup

Add your lyric files to `song_texts/`, audio files to `song_audios/`, and word lists to `language_texts/`, then run:
```bash
javac src/*.java
java -cp src SongPredictor
```

## How the Trie works

Every node tracks a `passCount` (words that passed through it) and `endCount` (words that end there). `mostLikelyNextChar` picks the child with the highest passCount, `mostLikelyNextWord` traverses all completions and returns whichever has the highest endCount. Song prediction works by building a separate Trie per song on startup, then checking word frequency across all of them when you finish typing a word.
