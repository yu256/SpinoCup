package app.Game

object RomajiParser:
  enum RomajiState:
    case Progress
    case Partial(jp: String, rest: String)
    case Complete(jp: String)
    case Invalid

  private val isVowel: Char => Boolean =
    Seq('a', 'i', 'u', 'e', 'o').contains

  val parseRomaji: String => RomajiState =
    parseLowerRomaji /* compose { _.toLowerCase } */ // 必ず小文字が入るため

  private def parseLowerRomaji(romaji: String): RomajiState =
    romaji.toList match {
      case 'n' :: 'n' :: Nil => RomajiState.Complete("ん")
      case c1 :: c2 :: Nil if c1 == c2 =>
        RomajiState.Partial("っ", c2.toString)
      case 'n' :: c2 :: Nil if !isVowel(c2) && c2 != 'y' /* nya ~ nyo */ =>
        RomajiState.Partial("ん", c2.toString)
      case _ =>
        romajiMap
          .collectFirst {
            case (k, v) if k.startsWith(romaji) =>
              if romaji == k then RomajiState.Complete(v)
              else RomajiState.Progress
          }
          .getOrElse(RomajiState.Invalid)
    }

  private val romajiMap = Map(
      "a" -> "あ",
      "i" -> "い",
      "u" -> "う",
      "e" -> "え",
      "o" -> "お",
      "ka" -> "か",
      "ki" -> "き",
      "ku" -> "く",
      "ke" -> "け",
      "ko" -> "こ",
      "kya" -> "きゃ",
      "kyi" -> "きぃ",
      "kyu" -> "きゅ",
      "kye" -> "きぇ",
      "kyo" -> "きょ",
      "sa" -> "さ",
      "si" -> "し",
      "su" -> "す",
      "se" -> "せ",
      "so" -> "そ",
      "sha" -> "しゃ",
      "shi" -> "し",
      "shu" -> "しゅ",
      "she" -> "しぇ",
      "sho" -> "しょ",
      "ta" -> "た",
      "ti" -> "ち",
      "chi" -> "ち",
      "tsu" -> "つ",
      "tu" -> "つ",
      "te" -> "て",
      "to" -> "と",
      "cha" -> "ちゃ",
      "tya" -> "ちゃ",
      "chu" -> "ちゅ",
      "tyu" -> "ちゅ",
      "che" -> "ちぇ",
      "tye" -> "ちぇ",
      "cho" -> "ちょ",
      "tyo" -> "ちょ",
      "na" -> "な",
      "ni" -> "に",
      "nu" -> "ぬ",
      "ne" -> "ね",
      "no" -> "の",
      "nya" -> "にゃ",
      "nyi" -> "にぃ",
      "nyu" -> "にゅ",
      "nye" -> "にぇ",
      "nyo" -> "にょ",
      "ha" -> "は",
      "hi" -> "ひ",
      "fu" -> "ふ",
      "hu" -> "ふ",
      "he" -> "へ",
      "ho" -> "ほ",
      "hya" -> "ひゃ",
      "hyu" -> "ひゅ",
      "hye" -> "ひぇ",
      "hyo" -> "ひょ",
      "ma" -> "ま",
      "mi" -> "み",
      "mu" -> "む",
      "me" -> "め",
      "mo" -> "も",
      "mya" -> "みゃ",
      "myu" -> "みゅ",
      "myi" -> "みぃ",
      "mye" -> "みぇ",
      "myo" -> "みょ",
      "ya" -> "や",
      "yu" -> "ゆ",
      "yo" -> "よ",
      "ra" -> "ら",
      "ri" -> "り",
      "ru" -> "る",
      "re" -> "れ",
      "ro" -> "ろ",
      "rya" -> "りゃ",
      "ryu" -> "りゅ",
      "ryi" -> "りぃ",
      "rye" -> "りぇ",
      "ryo" -> "りょ",
      "wa" -> "わ",
      "wi" -> "うぃ",
      "we" -> "うぇ",
      "wo" -> "を",
      "ga" -> "が",
      "gi" -> "ぎ",
      "gu" -> "ぐ",
      "ge" -> "げ",
      "go" -> "ご",
      "gya" -> "ぎゃ",
      "gyu" -> "ぎゅ",
      "gyi" -> "ぎぃ",
      "gye" -> "ぎぇ",
      "gyo" -> "ぎょ",
      "za" -> "ざ",
      "ji" -> "じ",
      "zu" -> "ず",
      "ze" -> "ぜ",
      "zo" -> "ぞ",
      "jya" -> "じゃ",
      "jyu" -> "じゅ",
      "jyi" -> "じぃ",
      "jye" -> "じぇ",
      "jyo" -> "じょ",
      "ja" -> "じゃ",
      "ju" -> "じゅ",
      "je" -> "じぇ",
      "jo" -> "じょ",
      "da" -> "だ",
      "di" -> "ぢ",
      "du" -> "づ",
      "de" -> "で",
      "do" -> "ど",
      "dya" -> "ぢゃ",
      "dyu" -> "ぢゅ",
      "dyi" -> "ぢぃ",
      "dye" -> "ぢぇ",
      "dyo" -> "ぢょ",
      "ba" -> "ば",
      "bi" -> "び",
      "bu" -> "ぶ",
      "be" -> "べ",
      "bo" -> "ぼ",
      "bya" -> "びゃ",
      "byu" -> "びゅ",
      "byi" -> "びぃ",
      "bye" -> "びぇ",
      "byo" -> "びょ",
      "pa" -> "ぱ",
      "pi" -> "ぴ",
      "pu" -> "ぷ",
      "pe" -> "ぺ",
      "po" -> "ぽ",
      "pya" -> "ぴゃ",
      "pyi" -> "ぴぃ",
      "pyu" -> "ぴゅ",
      "pye" -> "ぴぇ",
      "pyo" -> "ぴょ",
      "fa" -> "ふぁ",
      "fi" -> "ふぃ",
      "fe" -> "ふぇ",
      "fo" -> "ふぉ",
      "la" -> "ぁ",
      "li" -> "ぃ",
      "lu" -> "ぅ",
      "le" -> "ぇ",
      "lo" -> "ぉ",
      "xa" -> "ぁ",
      "xi" -> "ぃ",
      "xu" -> "ぅ",
      "xe" -> "ぇ",
      "xo" -> "ぉ",
      "ltu" -> "っ",
      "xtu" -> "っ",
      "ltsu" -> "っ",
      "lya" -> "ゃ",
      "lyu" -> "ゅ",
      "lyo" -> "ょ",
      "xya" -> "ゃ",
      "xyu" -> "ゅ",
      "xyo" -> "ょ",
      "lka" -> "ヵ",
      "lke" -> "ヶ",
      "xka" -> "ヵ",
      "xke" -> "ヶ",
      "-" -> "ー"
  )
