use regex::Regex;
use std::collections::HashMap;
use std::fs::read_to_string;

fn read_lines(filename: &str) -> Vec<String> {
    let mut result = Vec::new();

    for line in read_to_string(filename).unwrap().lines() {
        result.push(line.to_string())
    }

    result
}

fn line_to_pair(line: &str) -> (char, char) {
    let mut pair = ('x', 'y');
    for c in line.chars() {
        if c.is_numeric() {
            if pair.0 == 'x' {
                pair.0 = c;
            }
            pair.1 = c;
        }
    }
    return pair;
}

fn sum_lines<'a>(i: impl Iterator<Item = &'a str>) -> u16 {
    i.map(line_to_pair)
        .map(|p| format!("{}{}", p.0, p.1).parse::<u16>().unwrap())
        .into_iter()
        .sum()
}

fn first_num(line: &str) -> Result<String, &str> {
    let re = Regex::new(r"one|two|three|four|five|six|seven|eight|nine").unwrap();
    let mut string = String::new();
    for c in line.chars() {
        if c.is_numeric() {
            return Result::Ok(c.to_string());
        } else {
            string.push(c);
            let mtch = re.find(string.as_str());
            if mtch.is_some() {
                let word: &String = &mtch.unwrap().as_str().to_string();
                return Result::Ok(word.to_string());
            }
        }
    }
    Result::Err(line)
}

fn last_num(line: &str) -> Result<String, &str> {
    let re = Regex::new(r"one|two|three|four|five|six|seven|eight|nine").unwrap();
    let mut string = String::new();
    for c in line.chars().rev() {
        if c.is_numeric() {
            return Result::Ok(c.to_string());
        } else {
            string.insert(0, c);
            let mtch = re.find(string.as_str());
            if mtch.is_some() {
                let word: &String = &mtch.unwrap().as_str().to_string();
                return Result::Ok(word.to_string());
            }
        }
    }
    Result::Err(line)
}

fn sum_lines2<'a>(i: impl Iterator<Item = &'a str>) -> u16 {
    let mut number_word: HashMap<&str, &str> = HashMap::new();

    number_word.insert("one", "1");
    number_word.insert("two", "2");
    number_word.insert("three", "3");
    number_word.insert("four", "4");
    number_word.insert("five", "5");
    number_word.insert("six", "6");
    number_word.insert("seven", "7");
    number_word.insert("eight", "8");
    number_word.insert("nine", "9");

    let mut sum: u16 = 0;
    for line in i {
        let n1: &str;
        let first = first_num(line).unwrap();
        if first.len() > 1 {
            n1 = number_word.get(first.as_str()).unwrap();
        } else {
            n1 = first.as_str();
        }

        let n2: &str;
        let second = last_num(line).unwrap();
        if second.len() > 1 {
            n2 = number_word.get(second.as_str()).unwrap();
        } else {
            n2 = second.as_str();
        }

        sum += format!("{}{}", n1, n2).parse::<u16>().unwrap();
    }
    sum
}

pub(crate) fn solve() {
    let test_input = "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet";

    let sum = sum_lines(test_input.lines());

    println!("test input 1 result: {}", sum);

    let sum2 = sum_lines(read_lines("src/one.txt").iter().map(|s| s.as_str()));

    println!("input 1 result: {}", sum2);

    let test_input2 = "two1nine
    eightwothree
    abcone2threexyz
    xtwone3four
    4nineeightseven2
    zoneight234
    7pqrstsixteen";

    let sum3 = sum_lines2(test_input2.lines());

    println!("test input 2 result: {}", sum3);

    let sum4 = sum_lines2(read_lines("src/one.txt").iter().map(|s| s.as_str()));

    println!("input 1 result: {}", sum4);
}
